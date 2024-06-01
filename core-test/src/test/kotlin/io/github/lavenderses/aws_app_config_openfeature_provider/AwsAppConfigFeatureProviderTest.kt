package io.github.lavenderses.aws_app_config_openfeature_provider

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AwsAppConfigFeatureProviderTest {

    @InjectMocks
    private lateinit var awsAppConfigFeatureProvider: AwsAppConfigFeatureProvider

    @Mock
    private lateinit var awsAppConfigClientService: AwsAppConfigClientService
}
