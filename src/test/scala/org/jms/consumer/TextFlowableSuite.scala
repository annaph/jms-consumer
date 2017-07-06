package org.jms.consumer

import javax.jms.{Connection, Message, MessageConsumer, MessageListener}

import io.reactivex.Flowable
import io.reactivex.subscribers.TestSubscriber
import org.apache.activemq.command.ActiveMQTextMessage
import org.jms.consumer.TextConnectionProperties.{BROKER_URL, TOPIC_NAME}
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.{doNothing, verify, when}
import org.scalatest.junit.JUnitRunner
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSuite, Matchers}

import scala.collection.JavaConverters._
import scala.util.Try

/**
  * Test suite for [[TextFlowable]] instances.
  */
@RunWith(classOf[JUnitRunner])
class TextFlowableSuite extends FunSuite with Matchers with MockitoSugar {

  trait TestFixture {
    val msgFlowable: MsgFlowable = new TextFlowable(null, null)

    val mockConnection = mock[Connection]
    val mockMessageConsumer = mock[MessageConsumer]

    val jmsMsg1 = new ActiveMQTextMessage()
    val jmsMsg2 = new ActiveMQTextMessage()
    val jmsMsg3 = new ActiveMQTextMessage()
  }

  test("Prepare for emitting") {
    new TestFixture {
      doNothing().when(mockConnection)
        .start()
      doNothing().when(mockMessageConsumer)
        .setMessageListener(any(classOf[MessageListener]))

      msgFlowable prepare(mockConnection, mockMessageConsumer)

      val subscriber = new TestSubscriber[Message]()
      val flowable: Flowable[Message] = msgFlowable.messageFlowable()

      flowable subscribe subscriber
      val messageListener: MessageListener = msgFlowable.messageListener

      messageListener onMessage jmsMsg1
      messageListener onMessage jmsMsg2
      messageListener onMessage jmsMsg3

      subscriber assertValueCount 3
      subscriber assertNoErrors()
      subscriber.values().asScala should be(List(jmsMsg1, jmsMsg2, jmsMsg3))
    }
  }

  test("Prepare for emitting - handle failure") {
    new TestFixture {
      doNothing().when(mockConnection)
        .start()
      doNothing().when(mockMessageConsumer)
        .setMessageListener(any(classOf[MessageListener]))

      msgFlowable prepare(mockConnection, mockMessageConsumer)

      val subscriber = new TestSubscriber[Message]() {
        override def onNext(msg: Message): Unit = {
          throw new Exception()
        }
      }
      val flowable: Flowable[Message] = msgFlowable.messageFlowable()

      flowable subscribe subscriber
      val messageListener: MessageListener = msgFlowable.messageListener

      messageListener onMessage jmsMsg1

      subscriber.assertNoValues()
    }
  }

  test("Try to connect with missing connection properties") {
    val connectionProperties = mock[ConnectionProperties]
    when(connectionProperties.properties())
      .thenReturn(Map[String, String]())

    val msgFlowable = new TextFlowable(connectionProperties, null)

    val actual: Try[(Connection, MessageConsumer)] = msgFlowable.connect()

    actual.isFailure should be(true)
    actual.toEither.left.get.getMessage should be("Missing connection properties")
    verify(connectionProperties).properties()
  }

  test("Handle failure while establishing connection") {
    val connectionProperties = mock[ConnectionProperties]
    when(connectionProperties.properties())
      .thenReturn(Map(
        BROKER_URL -> "broker-url",
        TOPIC_NAME -> "topic-name"))

    val msgFlowable = new TextFlowable(connectionProperties, null)

    val actual: Try[(Connection, MessageConsumer)] = msgFlowable.connect()

    actual.isFailure should be(true)
    actual.toEither.left.get.getMessage should include("Transport not scheme specified: [broker-url]")
  }
}
