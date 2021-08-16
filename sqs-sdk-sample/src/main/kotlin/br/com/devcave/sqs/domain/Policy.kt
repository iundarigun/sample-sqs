package br.com.devcave.sqs.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class Policy(
    val version: String = "2012-10-17",
    val id: String,
    val statement: List<Statement> = listOf()
)

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class Statement(
    val sid: String,
    val effect: String = "Allow",
    val principal: Principal = Principal("*"),
    val action: String = "SQS:SendMessage",
    val resource: String,
    val condition: Condition
)

data class Principal(
    @JsonProperty("AWS")
    val aws: String
)

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class Condition(
    val arnLike: ArnLike
)

data class ArnLike(
    @JsonProperty("aws:SourceArn")
    val awsSourceArn: String
)