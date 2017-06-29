package org.jms.consumer

import java.io.ByteArrayOutputStream

import org.junit.runner.RunWith
import org.scalacheck.Prop.forAll
import org.scalacheck.{Gen, Prop}
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers
import org.scalatest.{FunSuite, Matchers}

import scalaz.effect.IO

/**
  * Test suite for [[TextProcessor]] instances.
  */
@RunWith(classOf[JUnitRunner])
class TextProcessorSuite extends FunSuite with Matchers with Checkers {

  trait TestFixture {
    val msgProcessor: MsgProcessor[String] = new TextProcessor()
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

}
