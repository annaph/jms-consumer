package org.jms.consumer

import javax.jms.JMSException

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class ConnectionExceptionListenerSuite extends FunSuite with Matchers {

  trait TestFixture {
    val exceptionListener = new ConnectionExceptionListener()
  }

  test("Handle connection failure") {
    new TestFixture {
      val exceptionMsg = "Connection exception"
      val jmsException = new JMSException(exceptionMsg)

      val caught = intercept[RuntimeException] {
        exceptionListener.onException(jmsException)
      }

      caught.getCause should be (jmsException)
      caught.getMessage should endWith (exceptionMsg)
    }
  }
}
