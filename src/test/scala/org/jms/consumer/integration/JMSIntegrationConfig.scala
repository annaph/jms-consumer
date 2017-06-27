package org.jms.consumer.integration

import javax.jms.{ExceptionListener, MessageProducer}

import org.jms.consumer._
import org.springframework.beans.factory.annotation.{Qualifier, Value}
import org.springframework.context.annotation.{Bean, Configuration, Profile}

@Configuration
@Profile(Array("test-jms"))
class JMSIntegrationConfig {

  @Bean(Array("test-connection-properties"))
  @Qualifier("test-connection-properties")
  def connectionProperties(
      @Value("${jms.activemq.host}") brokerURL: String,
      @Value("${jms.activemq.topic}") topicName: String): ConnectionProperties =
    new TextConnectionProperties(brokerURL, topicName)

  @Bean(Array("test-connection-exception-listener"))
  @Qualifier("test-connection-exception-listener")
  def exceptionListener(): ExceptionListener =
    new ConnectionExceptionListener()

  def messageFlowable(
      @Qualifier("test-connection-properties") connectionProperties: ConnectionProperties,
      @Qualifier("test-connection-exception-listener") exceptionListener: ExceptionListener): MsgFlowable = {
    val flowable: MsgFlowable = new TextFlowable(connectionProperties, exceptionListener)

    val (connection, messageConsumer) = flowable.connect()
    flowable prepare (connection, messageConsumer)

    flowable
  }

  def messageProducer(): MessageProducer = ???
}
