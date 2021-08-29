package br.com.devcave.sqs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration

@SpringBootApplication(exclude = [ContextInstanceDataAutoConfiguration::class])
class SqsSpringSampleApplication

fun main(args: Array<String>) {
    runApplication<SqsSpringSampleApplication>(*args)
}
