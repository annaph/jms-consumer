package org.jms.consumer

import java.lang.annotation.Annotation

import org.apache.activemq.command.{ActiveMQBytesMessage, ActiveMQTextMessage}
import org.junit.runner.RunWith
import org.scalacheck.{Gen, Prop}
import org.scalacheck.Prop.forAll
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import org.springframework.stereotype.Service

@RunWith(classOf[JUnitRunner])
class MsgConverterSuite extends FunSuite with Matchers with Checkers {

  trait TestFixture {
    val msgConverter = new MsgConverter()
  }

  test("Convert JMS message") {
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

  test("Convert non-supported JMS message") {
    new TestFixture {
      intercept[Exception] {
        val msg = new ActiveMQBytesMessage()
        msgConverter fromMessage msg
      }
    }
  }

  test("Compliant class annotations") {
    new TestFixture {
      val annotations: Array[Annotation] = msgConverter.getClass.getAnnotations
      val service: Service = msgConverter.getClass.getAnnotation(classOf[Service])

      service should not be (null)
    }
  }

}
