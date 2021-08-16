package br.com.devcave.sqs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SampleSqsApplication

fun main(args: Array<String>) {
	runApplication<SampleSqsApplication>(*args)
}
