package org.jms.consumer

import javax.jms.Session.AUTO_ACKNOWLEDGE
import javax.jms._

import io.reactivex.{BackpressureStrategy, Flowable, FlowableOnSubscribe}
import org.apache.activemq.ActiveMQConnectionFactory
import org.jms.consumer.TextConnectionProperties.{BROKER_URL, TOPIC_NAME}

import scala.util.{Failure, Success, Try}

trait MsgFlowable {

  def connect(): (Connection, MessageConsumer)

  def prepare(connection: Connection, messageConsumer: MessageConsumer)

  def messageFlowable(): Flowable[Message]

  private[consumer] var messageListener: MessageListener = _
}

class TextFlowable(
    private val connectionProperties: ConnectionProperties,
    private val connectionExceptionListener: ExceptionListener)
  extends MsgFlowable { f =>

  private var _flowable: Flowable[Message] = _

  override def connect(): (Connection, MessageConsumer) = {
    val props: Map[String, String] = connectionProperties.properties()

    val connectionFactory = new ActiveMQConnectionFactory(props(BROKER_URL))
    val connection: Connection = connectionFactory.createConnection()
    connection setExceptionListener connectionExceptionListener

    val session: Session = connection createSession (false, AUTO_ACKNOWLEDGE)
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
