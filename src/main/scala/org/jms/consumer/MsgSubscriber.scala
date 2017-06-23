package org.jms.consumer

import javax.jms.{Message, TextMessage}

import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

import scalaz.effect.IO

trait MsgSubscriber[M <: Message, D] {
  def subscribe(): Disposable
}

class Subscriber(
    observable: MsgObservable,
    converter: MsgConverter[String],
    processor: MsgProcessor[String])
  extends MsgSubscriber[TextMessage, String] {

  def subscribe(): Disposable = {
    val onNext: Consumer[Message] =
      msg => {
        val text = converter fromMessage msg.asInstanceOf[TextMessage]
        val action: IO[Unit] = processor process text

        action.unsafePerformIO()
      }

    val onError: Consumer[Throwable] =
      e => {
        e.printStackTrace()
      }

    observable.messageObsevable().subscribe(onNext, onError)
  }
}
