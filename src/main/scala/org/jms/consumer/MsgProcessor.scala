
/*                     __                                               *\
**     ________ ___   / /  ___     jms-consumer                         **
**    / __/ __// _ | / /  / _ |    (c) 2017                             **
**  __\ \/ /__/ __ |/ /__/ __ |                                         **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package org.jms.consumer

import scalaz.effect.IO
import scalaz.effect.IO.putStr

/**
  * Process JMS message that is represented as Scala object.
  *
  * Following objects use this trait:
  * [[TextProcessor]]
  *
  * @tparam M type of Scala object used to represent JMS message
  */
sealed trait MsgProcessor[M] {

  /**
    * Process message.
    *
    * @param msg message.
    * @return I/O action describing message processing.
    */
  def process(msg: M): IO[Unit]
}

/**
  * Process Text messages that is represented as [[String]] object.
  *
  * @constructor Creates new Text message processor.
  */
class TextProcessor extends MsgProcessor[String] {

  override def process(msg: String): IO[Unit] =
    putStr(msg)
}
