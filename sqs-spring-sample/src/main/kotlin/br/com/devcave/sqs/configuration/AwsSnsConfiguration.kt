package br.com.devcave.sqs.configuration

import br.com.devcave.sqs.domain.Queue
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.SetSubscriptionAttributesRequest
import com.amazonaws.services.sns.util.Topics
import com.amazonaws.services.sqs.AmazonSQS
import io.awspring.cloud.messaging.core.NotificationMessagingTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsSnsConfiguration(
    private val amazonSNS: AmazonSNS
) {

    @Bean
    fun sampleSns(amazonSQS: AmazonSQS, sampleQueue: Queue, secondQueue: Queue): String {
        val topic = amazonSNS.createTopic("sns-sample-topic")
        Topics.subscribeQueue(amazonSNS,amazonSQS, topic.topicArn, sampleQueue.queueUrl).also {
            val setAttributes = SetSubscriptionAttributesRequest().withSubscriptionArn(it)
                .withAttributeName("RawMessageDelivery")
                .withAttributeValue("true")
            amazonSNS.setSubscriptionAttributes(setAttributes)
        }
        Topics.subscribeQueue(amazonSNS,amazonSQS, topic.topicArn, secondQueue.queueUrl).also {
            val setAttributes = SetSubscriptionAttributesRequest().withSubscriptionArn(it)
                .withAttributeName("RawMessageDelivery")
                .withAttributeValue("true")
            amazonSNS.setSubscriptionAttributes(setAttributes)
        }
        return topic.topicArn
    }

    @Bean
    fun notificationMessagingTemplate(): NotificationMessagingTemplate = NotificationMessagingTemplate(amazonSNS)
}