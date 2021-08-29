package br.com.devcave.sqs.controller

import br.com.devcave.sqs.domain.Message
import org.slf4j.LoggerFactory
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("messages")
class AWSMessageController(
    private val queueMessagingTemplate: QueueMessagingTemplate,
    private val notificationMessagingTemplate: NotificationMessagingTemplate
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("sqs")
    fun producerOnSqs(@RequestBody request: Message) {
        logger.info("request: $request")
        queueMessagingTemplate.convertAndSend("sample-sqs-test", request)
    }

    @PostMapping("sqs/batch/{quantity}")
    fun producerBatchOnSqs(@PathVariable quantity: Int, @RequestBody request: Message) {
        logger.info("request: $request")
        repeat(quantity) {
            queueMessagingTemplate.convertAndSend("sample-sqs-test", request)
        }
    }
    @PostMapping("sns")
    fun producerOnSns(@RequestBody request: Message) {
        logger.info("request: $request")
        notificationMessagingTemplate.convertAndSend("sns-sample-topic", request)
    }
}