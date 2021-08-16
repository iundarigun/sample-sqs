package br.com.devcave.sqs.listener

import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

class QueueListener(
    private val sqsClient: SqsClient,
    private val queueUrl: String
) : Runnable {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run() {
        logger.info("Listener $queueUrl is running")
        while (true) {
            val receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .waitTimeSeconds(20)
                .build()
            val response = sqsClient.receiveMessage(receiveMessageRequest)
            if (!response.hasMessages()) {
                logger.info("queue has not messages")
            }
            response.messages().forEach {
                logger.info("$queueUrl - message Body: ${it.body()}")
                logger.info("$queueUrl - message Attributes: ${it.attributes()}")
                logger.info("$queueUrl - message id: ${it.messageId()}")
                logger.info("$queueUrl - message Message attributes: ${it.messageAttributes()}")
                logger.info("$queueUrl - message receiptHandle: ${it.receiptHandle()}")
                val deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(it.receiptHandle())
                    .build()
                sqsClient.deleteMessage(deleteRequest)
            }
        }
    }
}