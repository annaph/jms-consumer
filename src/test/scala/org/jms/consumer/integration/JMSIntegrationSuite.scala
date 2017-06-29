package org.jms.consumer.integration

import java.util.concurrent.TimeUnit.SECONDS
import javax.jms.{Message, MessageProducer}

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.TestSubscriber
import org.apache.activemq.command.ActiveMQTextMessage
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers.{equalTo, hasItems}
import org.hamcrest.MatcherAssert.assertThat
import org.jms.consumer.MsgFlowable
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitSuite
import org.springframework.beans.factory.annotation.{Autowired, Qualifier}
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.{ActiveProfiles, ContextConfiguration, TestPropertySource}

/**
  * Test suite to test consuming JMS message coming from embedded Apache ActiveMQ JMS instance.
  */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[JMSIntegrationConfig]))
@TestPropertySource(Array("/application.properties"))
@ActiveProfiles(Array("test-jms"))
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class JMSIntegrationSuite extends JUnitSuite {

  @Autowired
  @Qualifier("test-message-flowable")
  var messageFlowable: MsgFlowable = _

  @Autowired
  @Qualifier("test-message-producer")
  var messageProducer: MessageProducer = _

  trait TestFixture {
    val jmsMsg1: Message = new ActiveMQTextMessage()
    val jmsMsg2: Message = new ActiveMQTextMessage()
    val jmsMsg3: Message = new ActiveMQTextMessage()
  }

  @Test
  def testConsumingJMSMessages(): Unit = {
    new TestFixture {
      val subscriber = new TestSubscriber[Message]()
      val flowable: Flowable[Message] = messageFlowable.messageFlowable()

      flowable.subscribeOn(Schedulers.computation()) subscribe subscriber

      messageProducer send jmsMsg1
      messageProducer send jmsMsg2
      messageProducer send jmsMsg3

      await().timeout(12, SECONDS).until(() => subscriber.valueCount(), equalTo(3))

      subscriber.assertNoErrors()
      assertThat(subscriber.values(), hasItems(jmsMsg1, jmsMsg2, jmsMsg3))
    }
  }

  @Test
  def testConsuming10kJMSMessages(): Unit = {
    new TestFixture {
      val subscriber = new TestSubscriber[Message]()
      val flowable: Flowable[Message] = messageFlowable.messageFlowable()

      flowable.subscribeOn(Schedulers.computation()) subscribe subscriber

      val N = 10000
      for {
        _ <- 1 to N
      } messageProducer send jmsMsg1

      await().timeout(120, SECONDS).until(() => subscriber.valueCount(), equalTo(N))
      subscriber.assertNoErrors()
    }
  }
}
