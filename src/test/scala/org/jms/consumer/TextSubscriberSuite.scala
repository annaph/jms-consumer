package org.jms.consumer

import javax.jms.Message

import io.reactivex.Flowable
import org.apache.activemq.command.ActiveMQTextMessage
import org.junit.runner.RunWith
import org.mockito.Mockito.{doThrow, never, verify, when}
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.mockito.MockitoSugar

import scalaz.effect.IO.ioUnit

/**
  * Test suite for [[TextSubscriber]] instances.
  */
@RunWith(classOf[JUnitRunner])
class TextSubscriberSuite extends FunSuite with MockitoSugar {

  trait TestFixture {
    val mockMsgFlowable = mock[MsgFlowable]
    val mockMsgConverter = mock[MsgConverter[String]]
    val mockMsgProcessor = mock[MsgProcessor[String]]

    val subscriber: MsgSubscriber = new TextSubscriber(mockMsgFlowable, mockMsgConverter, mockMsgProcessor)

    val jmsMsg1 = new ActiveMQTextMessage()
    val text1 = "text1"
    jmsMsg1 setText text1

    val jmsMsg2 = new ActiveMQTextMessage()
    val text2 = "text2"
    jmsMsg2 setText text2

    val jmsMsg3 = new ActiveMQTextMessage()
    val text3 = "text3"
    jmsMsg3 setText text3

    val error = new RuntimeException("Subscriber exception")
  }

  test("Subscribe to flowable") {
    new TestFixture {
      val flowable: Flowable[Message] = Flowable.fromArray(jmsMsg1, jmsMsg2, jmsMsg3)

      when(mockMsgFlowable.messageFlowable())
        .thenReturn(flowable)

      when(mockMsgConverter.fromMessage(jmsMsg1))
        .thenReturn(text1)
      when(mockMsgProcessor.process(text1))
        .thenReturn(ioUnit)

      when(mockMsgConverter.fromMessage(jmsMsg2))
        .thenReturn(text2)
      when(mockMsgProcessor.process(text2))
        .thenReturn(ioUnit)

      when(mockMsgConverter.fromMessage(jmsMsg3))
        .thenReturn(text3)
      when(mockMsgProcessor.process(text3))
        .thenReturn(ioUnit)

      subscriber.subscribe()

      verify(mockMsgFlowable).messageFlowable()
      verify(mockMsgConverter).fromMessage(jmsMsg1)
      verify(mockMsgProcessor).process(text1)
      verify(mockMsgConverter).fromMessage(jmsMsg2)
      verify(mockMsgProcessor).process(text2)
      verify(mockMsgConverter).fromMessage(jmsMsg3)
      verify(mockMsgProcessor).process(text3)
    }
  }

  test("Subscribe to flowable - handle failure") {
    new TestFixture {
      val flowable: Flowable[Message] = Flowable.fromArray(jmsMsg1, jmsMsg2, jmsMsg3)

      when(mockMsgFlowable.messageFlowable())
        .thenReturn(flowable)

      when(mockMsgConverter.fromMessage(jmsMsg1))
        .thenReturn(text1)
      when(mockMsgProcessor.process(text1))
        .thenReturn(ioUnit)

      doThrow(error)
        .when(mockMsgConverter).fromMessage(jmsMsg2)

      when(mockMsgConverter.fromMessage(jmsMsg3))
        .thenReturn(text3)
      when(mockMsgProcessor.process(text3))
        .thenReturn(ioUnit)

      subscriber.subscribe()

      verify(mockMsgFlowable).messageFlowable()
      verify(mockMsgConverter).fromMessage(jmsMsg1)
      verify(mockMsgProcessor).process(text1)
      verify(mockMsgConverter).fromMessage(jmsMsg2)
      verify(mockMsgProcessor, never()).process(text2)
      verify(mockMsgConverter).fromMessage(jmsMsg3)
      verify(mockMsgProcessor).process(text3)
    }
  }
}
