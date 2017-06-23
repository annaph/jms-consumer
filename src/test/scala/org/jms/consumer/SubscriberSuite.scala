package org.jms.consumer

import javax.jms.{Message, TextMessage}

import io.reactivex.Observable
import org.apache.activemq.command.ActiveMQTextMessage
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.{never, verify, when}
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.mockito.MockitoSugar

import scalaz.effect.IO.ioUnit

@RunWith(classOf[JUnitRunner])
class SubscriberSuite extends FunSuite with MockitoSugar {

  trait TestFixture {
    val mockMsgObservable = mock[MsgObservable]
    val mockMsgConverter = mock[MsgConverter[String]]
    val mockMsgProcessor = mock[MsgProcessor[String]]

    val subscriber: MsgSubscriber[TextMessage, String] = new Subscriber(mockMsgObservable, mockMsgConverter, mockMsgProcessor)

    val jmsMsg1 = new ActiveMQTextMessage()
    val text1 = "text1"
    jmsMsg1 setText text1

    val jmsMsg2 = new ActiveMQTextMessage()
    val text2 = "text2"
    jmsMsg2 setText text2

    val exception = new RuntimeException()
  }

  test("Subscribe to observable") {
    new TestFixture {
      val observable: Observable[Message] = Observable.fromArray(jmsMsg1, jmsMsg2)

      when(mockMsgObservable.messageObsevable())
        .thenReturn(observable)

      when(mockMsgConverter.fromMessage(jmsMsg1))
        .thenReturn(text1)
      when(mockMsgProcessor.process(text1))
        .thenReturn(ioUnit)

      when(mockMsgConverter.fromMessage(jmsMsg2))
        .thenReturn(text2)
      when(mockMsgProcessor.process(text2))
        .thenReturn(ioUnit)

      subscriber.subscribe()

      verify(mockMsgObservable).messageObsevable()
      verify(mockMsgConverter).fromMessage(jmsMsg1)
      verify(mockMsgProcessor).process(text1)
      verify(mockMsgConverter).fromMessage(jmsMsg2)
      verify(mockMsgProcessor).process(text2)
    }
  }

  test("Subscribe to observable - handle error") {
    new TestFixture {
        val observable: Observable[Message] = Observable.error(exception)

        when(mockMsgObservable.messageObsevable())
          .thenReturn(observable)

        subscriber.subscribe()

        verify(mockMsgObservable).messageObsevable()
        verify(mockMsgConverter, never()).fromMessage(any[TextMessage])
        verify(mockMsgProcessor, never()).process(any[String])
      }
  }
}
