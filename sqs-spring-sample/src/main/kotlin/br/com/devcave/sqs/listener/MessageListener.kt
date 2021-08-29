package br.com.devcave.sqs.listener

import br.com.devcave.sqs.domain.Message
import org.slf4j.LoggerFactory
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class MessageListener {
    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener(value = ["sample-sqs-test", "second-sqs-test"])
    fun queueListener(message: Message, @Header("b3") tracing: String?) {
        logger.info("${Thread.currentThread().name} - Sample-sqs-test, $message, tracing-b3: $tracing")
    }
}