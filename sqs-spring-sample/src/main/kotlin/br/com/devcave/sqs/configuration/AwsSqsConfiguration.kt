package br.com.devcave.sqs.configuration

import br.com.devcave.sqs.domain.Queue
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.model.CreateQueueRequest
import io.awspring.cloud.messaging.core.QueueMessagingTemplate
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsSqsConfiguration(
    private val amazonSQS: AmazonSQSAsync
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun queueMessagingTemplate():QueueMessagingTemplate {
        return QueueMessagingTemplate(amazonSQS)
    }

    @Bean
    fun sampleQueue(): Queue {
        val queueName = "sample-sqs-test"
        return createQueue(queueName)
    }

    @Bean
    fun secondQueue(): Queue {
        val queueName = "second-sqs-test"
        return createQueue(queueName)
    }

    private fun createQueue(queueName: String): Queue{
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