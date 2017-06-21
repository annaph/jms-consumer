package org.jms.consumer

import java.io.ByteArrayOutputStream
import java.lang.annotation.Annotation

import org.junit.runner.RunWith
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Prop}
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import org.scalatest.{FunSuite, Matchers}
import org.springframework.stereotype.Service

import scalaz.effect.IO

@RunWith(classOf[JUnitRunner])
class MsgProcessorSuite extends FunSuite with Matchers with Checkers {

  trait TestFixture {
    val msgProcessor = new MsgProcessor()
  }

  test("Process text message") {
    new TestFixture {
      val propProcessMsg: Prop = forAll(Gen.alphaStr) { (msg: String) =>
        val action: IO[Unit] = msgProcessor process msg

        val output = new ByteArrayOutputStream()
        Console.withOut(output) {
          action.unsafePerformIO()
        }

        output.toString == msg
      }

      check(propProcessMsg)
    }
  }

  test("Compliant class annotations") {
    new TestFixture {
      val annotations: Array[Annotation] = msgProcessor.getClass.getAnnotations
      val service: Service = msgProcessor.getClass.getAnnotation(classOf[Service])

      service should not be (null)
    }
  }
}
