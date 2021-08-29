package br.com.devcave.sqs.configuration

import br.com.devcave.sqs.domain.Queue
import brave.Tracing
import brave.instrumentation.aws.sqs.SqsMessageTracing
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import com.amazonaws.services.sqs.model.CreateQueueRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.cloud.aws.autoconfigure.context.properties.AwsCredentialsProperties
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.converter.MessageConverter

@Configuration
class AwsSqsConfiguration {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun amazonSQS(awsCredentialsProperties: AwsCredentialsProperties): AmazonSQSAsync {
        val sqsMessageTracing = SqsMessageTracing.create(Tracing.current())
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
    fun messageConverter(objectMapper: ObjectMapper): MessageConverter {
        return MappingJackson2MessageConverter().also {
            it.objectMapper = objectMapper
        }
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