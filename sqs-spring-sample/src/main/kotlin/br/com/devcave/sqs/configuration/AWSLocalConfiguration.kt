package br.com.devcave.sqs.configuration

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConditionalOnProperty(
    name = ["aws.localstack"],
    havingValue = "true",
    matchIfMissing = false
)
@Configuration
class AWSLocalConfiguration(
    @Value("\${aws.local-url}")
    private val url: String
) {
    @Bean
    fun amazonSNS(): AmazonSNS =
        AmazonSNSAsyncClientBuilder
            .standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(url, "us-east-1"))
            .build()

    @Bean
    fun amazonSQS(): AmazonSQSAsync =
        AmazonSQSAsyncClientBuilder
            .standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(url, "us-east-1")).build()

}