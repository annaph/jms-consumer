package org.jms.consumer

import javax.jms.{Connection, Message, MessageConsumer, MessageListener}

import io.reactivex.{BackpressureStrategy, Flowable, FlowableOnSubscribe}

import scala.util.{Failure, Success, Try}

trait MsgFlowable {

  def prepare(connection: Connection, messageConsumer: MessageConsumer)

  def messageFlowable(): Flowable[Message]

  private[consumer] var messageListener: MessageListener = _
}

class TextFlowable(
    private val connectionProperties: ConnectionProperties,
    private val exceptionListener: ConnectionExceptionListener)
  extends MsgFlowable { f =>

  private var _flowable: Flowable[Message] = _

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
