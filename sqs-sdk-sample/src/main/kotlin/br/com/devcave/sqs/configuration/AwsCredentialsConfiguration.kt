package br.com.devcave.sqs.configuration

import br.com.devcave.sqs.listener.QueueListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import java.util.Queue
import javax.annotation.PostConstruct

@Component
class AwsCredentialsConfiguration(
    @Value("\${aws.access-key}")
    private val accessKey: String,
    @Value("\${aws.secret-key}")
    private val secretKey: String
) : AwsCredentialsProvider, AwsCredentials {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun resolveCredentials(): AwsCredentials = this

    override fun accessKeyId(): String = accessKey

    override fun secretAccessKey(): String = secretKey

}