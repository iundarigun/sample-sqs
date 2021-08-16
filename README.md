# SQS
This repo aims to test SQS and SQS with Spring boot.

Projects on this repo:
- sqs-sdk-sample: An implementation using SDK.
- sqs-spring-sample: An implementation using Spring Cloud AWS

The `sqs-spring-sample` can be run with localstack using profile `local`. To run it in local with docker:
```
docker run --rm -it -e SERVICES=sqs,sns -p 4566:4566 -p 4571:4571 localstack/localstack
```

On both projects, we need to configure credentials:
```yaml
## sqs-sdk-sample
aws:
  access-key: <access key>
  secret-key: <secret key>

## sqs-spring-sdk
cloud:
  aws:
    credentials:
      access-key: <access key>
      secret-key: <secret key>
    region:
      static: us-east-1
```

### Create SQS and SNS using console
To see how create an SQS and SNS using AWS console, you can see this repo: https://github.com/iundarigun/learning-aws#sqs-and-sns

---

## To Keep in mind

SQS has a timeout to "process" message. After this, the message returns to the queue and is available to other application get it.

### Short polling vs Long polling

We can open connection and wait for a time to receive message. If the time expires without messages, the get finish. If when is waiting arrive a message, we will get this message and the process finish without waiting the max time configured.

### Fifo queues
Try to keep order

### CLI
Some instructions to use SQS by CLI: 
```
aws sqs help
aws sqs queue-list
aws sqs send-message --queue-url https://sqs.us-east-1.amazonaws.com/654569462902/devcave-test --message-body "hello world"
aws sqs receive-message --queue-url https://sqs.us-east-1.amazonaws.com/654569462902/devcave-test
aws sqs delete-queue --queue-url https://sqs.us-east-1.amazonaws.com/654569462902/devcave-test
aws sqs create-queue --queue-name devcave-test
```
--- 

## References

- https://docs.awspring.io/spring-cloud-aws/docs/current/reference/html/index.html
- https://cursos.alura.com.br/course/aws-sqs-mensageria-desacoplamento-sistemas
- https://reflectoring.io/spring-cloud-aws-sqs/
- https://montivaljunior.medium.com/utilizando-spring-cloud-com-aws-sqs-e-localstack-d5bf66ea3151
- https://github.com/localstack/localstack