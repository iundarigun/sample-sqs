package br.com.devcave.sqs.listener

import br.com.devcave.sqs.domain.Message
import io.awspring.cloud.messaging.listener.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MessageListener {
    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener(value = ["sample-sqs-test", "second-sqs-test"])
    fun queueListener(message: Message) {
        logger.info("${Thread.currentThread().name} - Sample-sqs-test, $message")
    }
}