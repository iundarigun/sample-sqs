package br.com.devcave.sqs.configuration

import br.com.devcave.sqs.domain.ArnLike
import br.com.devcave.sqs.domain.Condition
import br.com.devcave.sqs.domain.Policy
import br.com.devcave.sqs.domain.Queue
import br.com.devcave.sqs.domain.Statement
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.CreateTopicRequest
import software.amazon.awssdk.services.sns.model.SubscribeRequest
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest
import software.amazon.awssdk.services.sqs.model.QueueAttributeName
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesRequest

@Configuration
class AwsSnsConfiguration(
    private val awsCredentialsConfiguration: AwsCredentialsConfiguration,
    private val queues: List<Queue>,
    private val sqsClient: SqsClient,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun snsClient(): SnsClient {
        return SnsClient.builder()
            .credentialsProvider(awsCredentialsConfiguration)
            .region(Region.US_EAST_1)
            .build()
    }

    @Bean
    fun sampleSns(): String {
        val request = CreateTopicRequest.builder()
            .name("sns-sample-topic")
            .build()
        val snsClient = snsClient()
        val topicArn = runCatching { snsClient.createTopic(request).topicArn() }
            .onSuccess { logger.info("Topic created") }
            .onFailure { logger.warn("Something worng with topic") }
            .getOrThrow()

        queues.forEach { subscribe(it, topicArn, snsClient) }

        return topicArn
    }

    private fun subscribe(queue: Queue, topicSns: String, snsClient: SnsClient) {
        val attributesRequest = GetQueueAttributesRequest.builder().queueUrl(queue.queueUrl)
            .attributeNames(QueueAttributeName.POLICY, QueueAttributeName.QUEUE_ARN).build()
        val attributes = sqsClient.getQueueAttributes(attributesRequest).attributes()

        val policy =
            attributes[QueueAttributeName.POLICY]?.let {
                objectMapper.readValue(it, Policy::class.java)
            } ?: Policy(id = "${attributes[QueueAttributeName.QUEUE_ARN]}/SQSDefaultPolicy")

        val statement = Statement(
            sid = "topic-subscription-$topicSns",
            resource = requireNotNull(attributes[QueueAttributeName.QUEUE_ARN]),
            condition = Condition(ArnLike(topicSns))
        )
        val statementList = policy.statement.filter { it.sid != statement.sid }.plus(statement)

        val setRequest = SetQueueAttributesRequest.builder()
            .queueUrl(queue.queueUrl)
            .attributes(
                mapOf(
                    QueueAttributeName.POLICY to objectMapper.writeValueAsString(policy.copy(statement = statementList))
                )
            )
            .build()

        sqsClient.setQueueAttributes(setRequest)

        val subscribeRequest = SubscribeRequest.builder()
            .protocol("sqs")
            .endpoint(requireNotNull(attributes[QueueAttributeName.QUEUE_ARN]))
            .topicArn(topicSns)
            .returnSubscriptionArn(true)
            .attributes(mapOf("RawMessageDelivery" to "true")) // This allow to send message directly to SQS, without wrapper
            .build()
        snsClient.subscribe(subscribeRequest)
    }
}