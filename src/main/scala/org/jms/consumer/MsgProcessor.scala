package org.jms.consumer

import org.springframework.stereotype.Service

import scalaz.effect.IO
import scalaz.effect.IO.putStr

@Service
class MsgProcessor {

  def process(msg: String): IO[Unit] =
    putStr(msg)
}
