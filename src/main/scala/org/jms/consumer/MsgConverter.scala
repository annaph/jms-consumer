package org.jms.consumer

import javax.jms._

sealed trait MsgConverter[D] {
  def fromMessage(msg: TextMessage): D
  def fromMessage(msg: BytesMessage): D
  def fromMessage(msg: MapMessage): D
  def fromMessage(msg: ObjectMessage): D
  def fromMessage(msg: StreamMessage): D
}

class TextConverter extends MsgConverter[String] {

  def fromMessage(msg: TextMessage): String =
    msg.getText()

  def fromMessage(msg: BytesMessage): String =
    throw new UnsupportedOperationException("Not supported")

  def fromMessage(msg: MapMessage): String =
    throw new UnsupportedOperationException("Not supported")

  def fromMessage(msg: ObjectMessage): String =
    throw new UnsupportedOperationException("Not supported")

  def fromMessage(msg: StreamMessage): String =
    throw new UnsupportedOperationException("Not supported")
}
