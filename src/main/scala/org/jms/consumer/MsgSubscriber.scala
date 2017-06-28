package org.jms.consumer

import javax.jms.{Message, TextMessage}

import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

import scalaz.effect.IO

trait MsgSubscriber {
  type M <: Message
  type D

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
