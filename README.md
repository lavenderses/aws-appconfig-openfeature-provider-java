# OpenFeature Provider for AWS AppConfig in Java

## Overview

Third party OpenFeature Provider implementation for AWS AppConfig on JVM platform, written in Java.

See OpenFeature documentation for what is OpenFeature.
And see also what is OpenFeature Provider.
- https://openfeature.dev/docs/reference/intro
- https://openfeature.dev/docs/reference/concepts/provider

See AWS AppConfig documentation for what is AWS AppConfig.
- https://docs.aws.amazon.com/appconfig/latest/userguide/what-is-appconfig.html

## Quick Start

First of all, install this library from Maven central repository.

The latest version is in here:
https://central.sonatype.com/artifact/io.github.lavenderses/aws-appconfig-openfeature-provider-java/overview

```gradle
dependencies {
    implementation 'io.github.lavenderses:aws-appconfig-openfeature-provider-java:0.3.0'

    // Also OpenFeature SDK for Java is required
    implementation 'dev.openfeature:sdk:1.8.0'
}
```

Now you are ready to connect AWS AppConfig with OpenFeature Java SDK.

To connect to AWS AppConfig using `AwsAppConfigFeatureProvider` (provider implementation for AWS AppConfig), configure 
`AwsAppConfigClientOptions` with valid AWS AppConfig configuration (application name, environment name and configuration
name).

```java
// provider configuration
AwsAppConfigClientOptions options = AwsAppConfigClientOptions.builder()
        .applicationName("AWS AppConfig application name")
        .environmentName("AWS AppConfig environment name")
        .profile("AWS AppConfig profile name")
        .awsAppConfigProxyConfig(
            // access AWS AppConfig via agent.
            AwsAppConfigAgentProxyConfig.builder()
                .endpoint(URI("http://localhost:2772"))
                .build()
        )
        .build();

// configure a provider
AwsAppConfigFeatureProvider provider = new AwsAppConfigFeatureProvider(options);
```

To use this provider, configure `provider` with `OpenFeatureApi#setProviderAndWait`.

```java
// configure provider
OpenFeatureApi api = OpenFeatureAPI.getInstance();
api.setProviderAndWait(provider);

// get client and use it to get feature flag value
Client client = agentAPi.getClient();
boolean flagValue = client.getBooleanValue("v2_enabled", false);
```

## Requirements

Java 17 or higher.

I'm preparing to support Java 8+.

## Usage

### 1. Configure AWS AppConfig

First of all, you have to set up AWS AppConfig instance.
As described in documentation, please create an application and an environment.

1. Create an application
   1. https://docs.aws.amazon.com/appconfig/latest/userguide/appconfig-creating-namespace.html
2. Create an environment
   1. https://docs.aws.amazon.com/appconfig/latest/userguide/appconfig-creating-environment.html

### 2. Create new feature flag

After setting up the application and the environment, you are ready to create new feature flag.

1. Create a feature flag with "a feature flag configuration profile".
    1. https://docs.aws.amazon.com/appconfig/latest/userguide/appconfig-creating-configuration-and-profile-feature-flags.html
2. Add new attribute with key name `flag_value`, and with value you want to use as feature flag.

**This provider implementation requires you to add this `flag_value` attribute to every flag.
If the attribute is not configured, this provider implementation regards the feature flag as disabled.**

Details is following table.

| configuration profile | enabled configuration profile | attribute                           | flag value returned from this provider      |
|-----------------------|-------------------------------|-------------------------------------|---------------------------------------------|
| exists                | enabled                       | `flag_value` exists                 | the `flag_value` you configured             |
| exists                | enabled                       | `flag_value` doesn't exist          | default value you passed to OpenFeature SDK |
| exists                | disabled                      | `flag_value` exists / doesn't exist | default value you passed to OpenFeature SDK |
| doesn't exist         | enabled / disabled            | `flag_value` exists / doesn'T exist | default value you passed to OpenFeature SDK |

### 3. Configure AWS AppConfig agent

To connect AWS AppConfig instance from your application, check the following document.

- https://docs.aws.amazon.com/appconfig/latest/userguide/appconfig-integration-ec2.html
  - get feature flag from EC2 instance
- https://docs.aws.amazon.com/appconfig/latest/userguide/appconfig-integration-containers-agent.html
    - get feature flag from ECS / EKS cluster

### 4. Get feature flag value in your application

You can know how to retrieve feature flag in [Quick Start](#quick-start) section.

Please use application name, environment name and profile name which you configured in 1 and 2.

### Notes

As described above, this provider implementation expects AWS AppConfig feature flag type with
`a feature flag configuration profile`, not `free form configuration profile`.

`free form configuration profile` will be ignored.

## Run on local

If you want to run it on local, AWS AppConfig supports local development.
- https://docs.aws.amazon.com/appconfig/latest/userguide/appconfig-retrieving-simplified-methods-local-development.html

If you have environment which can run Docker, you can do it.
AWS prepares AppConfig agent container for local development.
- https://gallery.ecr.aws/aws-appconfig/aws-appconfig-agent

### 1. Prepare feature flag file

The container watches specified (JSON) file as feature flag. So create new JSON file with the following schema in a
directory.

```json5
{
  "your feature flag key": {
    // If you want to enable this feature, set the value true.
    "enabled": boolean,
    // any type feature flag value you want to use.
    // to understand why this `flag_value` is required, see "Create new feature flag" section in this document.
    // actually this is an attribute in AWS AppConfig.
    "flag_value": your feature flag value,
  }
}
```

Disabled feature flag exampel with key name `key_1` and string feature flag value with value `you released`.

```json
{
  "key_1": {
    "enabled": false,
    "flag_value": "you not released"
  }
}
```

Save this file with name `{application name}:{environment name}:{profile-name}`.
- `{application name}`
- `{environment name}`
- `{profile name}`

For example, if you want to configure AWS AppConfig with following configuration, the file name will be `app:env:profile`.

- application name is `app`
- environment name is `env`
- profile name is `profile`

### 2. Run docker container

Once you have done 1, you are ready to run AWS AppConfig agent in local.

```shell
docker run \
  --rm \
  # container's default port is 2772
  -p 2772:2772
  # mount JSON file you created in 1 as volume
  # /path/to/directory-of-json-file is the directory path your feature flag file in.
  -v /path/to/directory-of-json-file:/home/www/app-config-value
  -e LOCAL_DEVELOPMENT_DIRECTORY="/home/www/app-config-value" \
  public.ecr.aws/aws-appconfig/aws-appconfig-agent:2.0.3296
```

### 3. Check your feature flag

If you have successfully run the container, let's get feature flag value from the agent container.

```shell
curl http://localhost:2772/applications/{application name}/environments/{environment name}/configurations/{configuration name}?flag={your feature flag key}
```

If you run with the above example, the HTTP request will be like following.

```shell
curl http://localhost:2772/applications/app/environments/env/configurations/profile??flag=key_1
```

You will get following JSON response. (This is exactly same as feature flag JSON file.)

```json
{
  "key_1": {
    "enabled": false,
    "flag_value": "you not released"
  }
}
```


### 4. Change the feature flag value and check it

If you want to experience dynamic flag value change, let's change the feature flag JSON file.

```json5
{
  "key_1": {
    // changed as true
    "enabled": true,
    // changed value
    "flag_value": "you released",
  }
}
```

After a while, you'll get new feature flag value.

```shell
curl http://localhost:2772/applications/app/environments/env/configurations/profile??flag=key_1

{
  "key_1": {
    "enabled": true,
    "flag_value": "you released"
  }
}
```

See more for [example](./example) directory.

## LICENSE

Apache License 2.0

See [LICENSE](./LICENSE).

## Contributing

I'm welcome to any contribution.

See [CONTRIBUTING.md](./CONTRIBUTING.md) to know how to develop and contribute.

## Contact

Feel free to contact me in [GitHub issue](https://github.com/lavenderses/AWSAppConfig-OpenFeature-provider-java/issues)
or [@lavenderses on Twitter](https://twitter.com/lavenderses).
