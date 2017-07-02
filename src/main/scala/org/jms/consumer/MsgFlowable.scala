/*                     __                                               *\
**     ________ ___   / /  ___     jms-consumer                         **
**    / __/ __// _ | / /  / _ |    (c) 2017                             **
**  __\ \/ /__/ __ |/ /__/ __ |                                         **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package org.jms.consumer

import javax.jms.Session.AUTO_ACKNOWLEDGE
import javax.jms._

import io.reactivex.{BackpressureStrategy, Flowable, FlowableOnSubscribe}
import org.apache.activemq.ActiveMQConnectionFactory
import org.jms.consumer.TextConnectionProperties.{BROKER_URL, TOPIC_NAME}

import scala.util.{Failure, Success, Try}

/**
  * Establish connection with external JMS system, prepares for receiving JMS messages and emits them to any subscribed component.
  *
  * Following objects use this trait:
  * [[TextFlowable]]
  *
  */
trait MsgFlowable {

  /**
    * Establish connection with external JMS system. Returns JMS connection and consumer of JMS messages.
    *
    * @return pair of connection and message consumer.
    */
  def connect(): (Connection, MessageConsumer)

  /**
    * Prepares for receiving JMS messages and emitting them to any subscribed component.
    * Requires JMS connection and JMS message consumer.
    *
    * @param connection      JMS connection.
    * @param messageConsumer JMS message consumer.
    */
  def prepare(connection: Connection, messageConsumer: MessageConsumer)

  /**
    * Returns [[Flowable]] that emits JMS messages to any subscribed component.
    *
    * @return flowable.
    */
  def messageFlowable(): Flowable[Message]

  private[consumer] var messageListener: MessageListener = _
}

/**
  * [[MsgFlowable]] used to establish connection with Apache ActiveMQ JMS system.
  *
  * @constructor Creates new Text flowable.
  * @param connectionProperties        connection properties required to establish connection with JMS system.
  * @param connectionExceptionListener JMS connection exception listener.
  */
class TextFlowable(
                    private val connectionProperties: ConnectionProperties,
                    private val connectionExceptionListener: ExceptionListener)
  extends MsgFlowable {
  f =>

  private var _flowable: Flowable[Message] = _

  override def connect(): (Connection, MessageConsumer) = {
    val props: Map[String, String] = connectionProperties.properties()

    val connectionFactory = new ActiveMQConnectionFactory(props(BROKER_URL))
    val connection: Connection = connectionFactory.createConnection()
    connection setExceptionListener connectionExceptionListener

    val session: Session = connection createSession(false, AUTO_ACKNOWLEDGE)
    val topic: Topic = session createTopic props(TOPIC_NAME)
    val messageConsumer: MessageConsumer = session createConsumer topic

    connection -> messageConsumer
  }

  override def prepare(connection: Connection, messageConsumer: MessageConsumer): Unit = {
    val source: FlowableOnSubscribe[Message] = {
      subscriber =>
        val listener: MessageListener = {
          (msg: Message) =>
            Try {
              subscriber onNext msg
            } match {
              case Success(_) =>
                ()
              case Failure(e) =>
                subscriber onError e
            }
        }

        f.messageListener = listener
        messageConsumer setMessageListener listener

        connection.start()
    }

    _flowable = Flowable create(source, BackpressureStrategy.BUFFER)
  }

  override def messageFlowable(): Flowable[Message] =
    _flowable
}
