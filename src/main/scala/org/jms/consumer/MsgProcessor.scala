package org.jms.consumer

import scalaz.effect.IO
import scalaz.effect.IO.putStr

sealed trait MsgProcessor[M] {
  def process(msg: M): IO[Unit]
}

class TextProcessor extends MsgProcessor[String] {

  def process(msg: String): IO[Unit] =
    putStr(msg)
}
