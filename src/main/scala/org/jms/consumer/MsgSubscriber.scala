package org.jms.consumer

import javax.jms.{Message, TextMessage}

import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

import scalaz.effect.IO

/**
  * Subscribes to a [[MsgFlowable]] and handles the items it emits and any error notification it issues.
  *
  */
trait MsgSubscriber {
  /** Type of JMS message */
  type M <: Message

  /** Type of Scala object used to represent JMS message */
  type D

  /**
    * 
    * @return
    */
  def subscribe(): Disposable
}

class TextSubscriber(
                  flowable: MsgFlowable,
                  converter: MsgConverter[String],
                  processor: MsgProcessor[String])
  extends MsgSubscriber {

  type M = TextMessage
  type D = String

  def subscribe(): Disposable = {
    val onNext: Consumer[Message] =
      msg => {
        val text = converter fromMessage msg.asInstanceOf[TextMessage]
        val action: IO[Unit] = processor process text

        action.unsafePerformIO()
        println
      }

    val onError: Consumer[Throwable] =
      e => {
        e.printStackTrace()
      }

    flowable.messageFlowable().subscribe(onNext, onError)
  }
}
