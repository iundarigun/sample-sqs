package br.com.devcave.sqs.configuration

import br.com.devcave.sqs.domain.Queue
import br.com.devcave.sqs.listener.QueueListener
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.iam.model.Statement
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import software.amazon.awssdk.services.sqs.model.QueueAttributeName
import javax.annotation.PostConstruct

@Configuration
class AwsSqsConfiguration(
    private val awsCredentialsConfiguration: AwsCredentialsConfiguration
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun sqsClient(): SqsClient {
        return SqsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(awsCredentialsConfiguration)
            .build()
    }

    @Bean
    fun sampleQueue(): Queue {
        val sqsClient = sqsClient()
        val queueName = "sample-sqs-test"
        val queue = getOrCreateQueue(queueName, sqsClient)
        Thread(QueueListener(sqsClient, queue.queueUrl)).start()
        return queue
    }

    @Bean
    fun secondQueue(): Queue {
        val sqsClient = sqsClient()
        val queueName = "second-sqs-test"
        val queue = getOrCreateQueue(queueName, sqsClient)
        Thread(QueueListener(sqsClient, queue.queueUrl)).start()
        return queue
    }

    private fun getOrCreateQueue(
        queueName: String,
        sqsClient: SqsClient
    ): Queue {
        val queue = CreateQueueRequest.builder()
            .queueName(queueName)
            .build()

        return runCatching { sqsClient.createQueue(queue).queueUrl() }
            .onSuccess { logger.info("Queue created!") }
            .onFailure { logger.warn("Queue already exists") }
            .getOrElse {
                val request = GetQueueUrlRequest.builder().queueName(queueName).build()
                sqsClient.getQueueUrl(request).queueUrl()
            }.let {
                Queue(queueName, it)
            }
    }
}