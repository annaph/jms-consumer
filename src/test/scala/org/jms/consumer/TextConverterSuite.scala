package org.jms.consumer

import org.apache.activemq.command._
import org.junit.runner.RunWith
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Prop}
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import org.scalatest.{FunSuite, Matchers}

/**
  * Test suite for [[TextConverter]] instances.
  */
@RunWith(classOf[JUnitRunner])
class TextConverterSuite extends FunSuite with Matchers with Checkers {

  trait TestFixture {
    val msgConverter: MsgConverter[String] = new TextConverter()
  }

  test("Convert text JMS message") {
    new TestFixture {
      val propConvertMsg: Prop = forAll(Gen.alphaStr) { (text: String) =>
        val msg = new ActiveMQTextMessage()
        msg.setText(text)

        val str: String = msgConverter fromMessage msg

        str == text
      }

      check(propConvertMsg)
    }
  }

  test("Convert non-supported bytes JMS message") {
    new TestFixture {
      intercept[UnsupportedOperationException] {
        val msg = new ActiveMQBytesMessage()
        msgConverter fromMessage msg
      }
    }
  }

  test("Convert non-supported map JMS message") {
    new TestFixture {
      intercept[UnsupportedOperationException] {
        val msg = new ActiveMQMapMessage()
        msgConverter fromMessage msg
      }
    }
  }

  test("Convert non-supported object JMS message") {
    new TestFixture {
      intercept[UnsupportedOperationException] {
        val msg = new ActiveMQObjectMessage()
        msgConverter fromMessage msg
      }
    }
  }

  test("Convert non-supported stream JMS message") {
    new TestFixture {
      intercept[UnsupportedOperationException] {
        val msg = new ActiveMQStreamMessage()
        msgConverter fromMessage msg
      }
    }
  }

}
