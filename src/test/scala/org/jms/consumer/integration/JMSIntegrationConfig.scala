package org.jms.consumer.integration

import javax.jms.DeliveryMode.NON_PERSISTENT
import javax.jms.Session.AUTO_ACKNOWLEDGE
import javax.jms._

import org.apache.activemq.ActiveMQConnectionFactory
import org.jms.consumer.TextConnectionProperties.{BROKER_URL, TOPIC_NAME}
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

  @Bean(Array("test-message-flowable"))
  @Qualifier("test-message-flowable")
  def messageFlowable(
      @Qualifier("test-connection-properties") connectionProperties: ConnectionProperties,
      @Qualifier("test-connection-exception-listener") exceptionListener: ExceptionListener): MsgFlowable = {
    val flowable: MsgFlowable = new TextFlowable(connectionProperties, exceptionListener)

    val (connection, messageConsumer) = flowable.connect()
    flowable prepare (connection, messageConsumer)

    flowable
  }

  @Bean(Array("test-message-producer"))
  @Qualifier("test-message-producer")
  def messageProducer(
      @Qualifier("test-connection-properties") connectionProperties: ConnectionProperties,
      @Qualifier("test-connection-exception-listener") exceptionListener: ExceptionListener): MessageProducer = {
    val props: Map[String, String] = connectionProperties.properties()

    val connectionFactory = new ActiveMQConnectionFactory(props(BROKER_URL))

    val connection: Connection = connectionFactory.createConnection()
    connection setExceptionListener exceptionListener

    val session: Session = connection createSession (false, AUTO_ACKNOWLEDGE)
    val topic: Topic = session createTopic props(TOPIC_NAME)

    val messageProducer: MessageProducer = session createProducer topic
    messageProducer setDeliveryMode NON_PERSISTENT

    messageProducer
  }
}
