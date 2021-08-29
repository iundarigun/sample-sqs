package br.com.devcave.sqs.configuration

import br.com.devcave.sqs.domain.Queue
import brave.Tracing
import brave.instrumentation.aws.sqs.SqsMessageTracing
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import com.amazonaws.services.sqs.model.CreateQueueRequest
import io.awspring.cloud.autoconfigure.context.properties.AwsCredentialsProperties
import io.awspring.cloud.messaging.core.QueueMessagingTemplate
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsSqsConfiguration(
    private val tracing: Tracing
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    // This bean can be remove if don't need add traceid to message
    @Bean
    fun amazonSQS(awsCredentialsProperties: AwsCredentialsProperties): AmazonSQSAsync {
        val sqsMessageTracing = SqsMessageTracing.create(tracing)

        return AmazonSQSAsyncClientBuilder.standard()
            .withRequestHandlers(sqsMessageTracing.requestHandler())
            .withCredentials(
                AWSStaticCredentialsProvider(object : AWSCredentials {
                    override fun getAWSAccessKeyId(): String {
                        return awsCredentialsProperties.accessKey
                    }

                    override fun getAWSSecretKey(): String {
                        return awsCredentialsProperties.secretKey
                    }

                })
            ).withRegion("us-east-1")
            .build()
    }

    @Bean
    fun queueMessagingTemplate(amazonSQS: AmazonSQSAsync): QueueMessagingTemplate {
        return QueueMessagingTemplate(amazonSQS)
    }

    @Bean
    fun sampleQueue(amazonSQS: AmazonSQSAsync): Queue {
        val queueName = "sample-sqs-test"
        return createQueue(amazonSQS, queueName)
    }

    @Bean
    fun secondQueue(amazonSQS: AmazonSQSAsync): Queue {
        val queueName = "second-sqs-test"
        return createQueue(amazonSQS, queueName)
    }

    private fun createQueue(amazonSQS: AmazonSQSAsync, queueName: String): Queue {
        val request = CreateQueueRequest()
            .withQueueName(queueName)
        return runCatching { amazonSQS.createQueue(request) }
            .onSuccess { logger.info("$queueName: Create success") }
            .onFailure { logger.warn("Error creating queue") }
            .getOrThrow().let {
                Queue(queueName, it.queueUrl)
            }
    }
}