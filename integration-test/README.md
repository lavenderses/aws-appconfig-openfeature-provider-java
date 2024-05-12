# Integration Test

## For what

This is for testing this provider implementation in actual environment.
Also, to show how to use this provider implementation in an application (to Application Author).

This integration test will be executed on main merged.
But you can do it on your local computer.

## How to run

- **note that you are in root dir of this repo**

### build application image

```shell
./gradlew :integration-test:app:jibDockerBuild
```

### Run docker container

```shell
docker compose -f integration-test/docker-compose.yaml up
```

### Do integration test

```shell
./gradlew :integration-test:testing:test
```

### Clean up containers

```shell
docker compose -f integration-test/docker-compose.yaml down
```
