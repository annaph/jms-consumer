package org.jms.consumer

import org.springframework.beans.factory.annotation.{Qualifier, Value}
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, Configuration, Profile}

@Configuration
@Profile(Array("prod"))
class DemoMsgConsumerConfig {

  @Bean(Array("text-connection-properties"))
  @Qualifier("text-connection-properties")
  def connectionProperties(
                            @Value("${jms.activemq.host}") brokerURL: String,
                            @Value("${jms.activemq.topic}") topicName: String): ConnectionProperties =
    new TextConnectionProperties(brokerURL, topicName)

  @Bean(Array("text-connection-exception-listener"))
  @Qualifier("text-connection-exception-listener")
  def connectionExceptionListener(): ConnectionExceptionListener =
    new ConnectionExceptionListener()

  @Bean(Array("text-converter"))
  @Qualifier("text-converter")
  def textConverter(): MsgConverter[String] =
    new TextConverter()

  @Bean(Array("text-processor"))
  @Qualifier("text-processor")
  def textProcessor(): MsgProcessor[String] =
    new TextProcessor()

  @Bean(Array("text-flowable"))
  @Qualifier("text-flowable")
  def textFlowable(
                    @Qualifier("text-connection-properties") connectionProperties: ConnectionProperties,
                    @Qualifier("text-connection-exception-listener") connectionExceptionListener: ConnectionExceptionListener): MsgFlowable = {
    val flowable = new TextFlowable(connectionProperties, connectionExceptionListener)

    val (connection, messageConsumer) = flowable.connect()
    flowable prepare(connection, messageConsumer)

    flowable
  }

  @Bean(Array("text-subscriber"))
  @Qualifier("text-subscriber")
  def textSubscriber(
                      @Qualifier("text-flowable") msgFlowable: MsgFlowable,
                      @Qualifier("text-converter") msgConverter: MsgConverter[String],
                      @Qualifier("text-processor") msgProcessor: MsgProcessor[String]): MsgSubscriber = {
    val subscriber: MsgSubscriber = new TextSubscriber(msgFlowable, msgConverter, msgProcessor)
    subscriber.subscribe()

    subscriber
  }

}

@SpringBootApplication
object DemoMsgConsumerApplication extends App {
  SpringApplication run classOf[DemoMsgConsumerConfig]
}
