package br.com.devcave.sqs.controller

import br.com.devcave.sqs.domain.ArnLike
import br.com.devcave.sqs.domain.Condition
import br.com.devcave.sqs.domain.Message
import br.com.devcave.sqs.domain.Policy
import br.com.devcave.sqs.domain.Principal
import br.com.devcave.sqs.domain.Queue
import br.com.devcave.sqs.domain.Statement
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

@RestController
@RequestMapping("messages")
class SQSController(
    private val sqsClient: SqsClient,
    private val snsClient: SnsClient,
    private val sampleQueue: Queue,
    private val sampleSns: String
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("policy")
    fun randomPolicy(): Policy {
        return Policy("2012", "xpyo", listOf(
            Statement("isdsd", "Allow", Principal("*"),
            "SQS:SendMessage",
            "xpto2",
            Condition(ArnLike("sdsds"))
            )
        ))
    }

    @PostMapping("sqs")
    fun producerOnSqs(@RequestBody request: Message) {
        logger.info("request: $request")
        sendMessageToSQS(request)
    }

    @PostMapping("sns")
    fun producerOnSns(@RequestBody request: Message) {
        logger.info("request: $request")
        sendMessageToSNS(request)
    }

    private fun sendMessageToSQS(message: Message) {
        val request = SendMessageRequest.builder()
            .queueUrl(sampleQueue.queueUrl)
            .messageBody(message.body)
            .build()
        val sendMessageResponse = sqsClient.sendMessage(request)
        logger.info("messageResponse $sendMessageResponse")
    }

    private fun sendMessageToSNS(message: Message){
        val request = PublishRequest.builder()
            .message(message.body)
            .topicArn(sampleSns)
            .build()
        val publish = snsClient.publish(request)
        logger.info("publishResponse, $publish")
    }
}