package org.jms.consumer

import org.springframework.beans.factory.annotation.{Qualifier, Value}
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, Configuration, Profile}

/**
  * Spring configuration class used to create beans at runtime.
  * This configuration can be used only when 'prod' profile is active.
  *
  * @constructor Creates new Spring configuration object.
  */
@Configuration
@Profile(Array("prod"))
class DemoMsgConsumerConfig {

  /**
    * Creates new [[ConnectionProperties]] object.
    *
    * @param brokerURL Apache Active MQ broker URL.
    * @param topicName name of the topic to subscribe to.
    * @return Text connection properties.
    */
  @Bean(Array("text-connection-properties"))
  @Qualifier("text-connection-properties")
  def connectionProperties(
                            @Value("${jms.activemq.host}") brokerURL: String,
                            @Value("${jms.activemq.topic}") topicName: String): ConnectionProperties =
  new TextConnectionProperties(brokerURL, topicName)

  /**
    * Creates new [[ConnectionExceptionListener]] object.
    *
    * @return connection exception listener.
    */
  @Bean(Array("text-connection-exception-listener"))
  @Qualifier("text-connection-exception-listener")
  def connectionExceptionListener(): ConnectionExceptionListener =
  new ConnectionExceptionListener()

  /**
    * Creates new [[MsgConverter]] object.
    *
    * @return Text message converter.
    */
  @Bean(Array("text-converter"))
  @Qualifier("text-converter")
  def textConverter(): MsgConverter[String] =
  new TextConverter()

  /**
    * Creates new [[MsgProcessor]] object.
    *
    * @return Text message processor.
    */
  @Bean(Array("text-processor"))
  @Qualifier("text-processor")
  def textProcessor(): MsgProcessor[String] =
  new TextProcessor()

  /**
    * Creates new [[ConnectionProperties]] object.
    *
    * @param connectionProperties        connection properties.
    * @param connectionExceptionListener connection exception listener.
    * @return Text flowable.
    */
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

  /**
    * Creates new [[MsgSubscriber]] object.
    *
    * @param msgFlowable  message flowable.
    * @param msgConverter message converter.
    * @param msgProcessor message processor.
    * @return Text subscriber.
    */
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

/**
  * Application entry point.
  */
@SpringBootApplication
object DemoMsgConsumerApplication extends App {
  SpringApplication run classOf[DemoMsgConsumerConfig]
}
