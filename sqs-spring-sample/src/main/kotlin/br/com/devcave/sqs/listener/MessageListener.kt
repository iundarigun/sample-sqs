package br.com.devcave.sqs.listener

import br.com.devcave.sqs.domain.Message
import brave.Tracer
import brave.propagation.TraceContext
import io.awspring.cloud.messaging.listener.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import java.math.BigInteger

@Component
class MessageListener(
    private val tracer: Tracer
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener(value = ["sample-sqs-test", "second-sqs-test"])
    fun queueListener(message: Message, @Header("b3") tracing: String?) {
        tracing?.let { enableSleuth(it) }
        logger.info("${Thread.currentThread().name} - Sample-sqs-test, $message, tracing-b3: $tracing")
    }

    private fun enableSleuth(tracing: String) {
        val numberTraceId = BigInteger(tracing.split("-")[0], 16).toLong()
        val numberSpanId = BigInteger(tracing.split("-")[1], 16).toLong()
        val trace = TraceContext.newBuilder()
            .traceId(numberTraceId)
            .spanId(numberSpanId)
            .build()

        tracer.startScopedSpanWithParent(Thread.currentThread().name, trace)
    }
}