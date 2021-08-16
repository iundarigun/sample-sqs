package br.com.devcave.sqs.domain

data class Queue(
    val name: String,
    val queueUrl: String
)