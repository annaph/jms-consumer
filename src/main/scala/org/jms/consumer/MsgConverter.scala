/*                     __                                               *\
**     ________ ___   / /  ___     jms-consumer                         **
**    / __/ __// _ | / /  / _ |    (c) 2017                             **
**  __\ \/ /__/ __ |/ /__/ __ |                                         **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package org.jms.consumer

import javax.jms._

/**
  * Converts JMS message to Scala object.
  *
  * Following objects use this trait:
  * [[TextConverter]]
  *
  * @tparam D type of Scala object to convert JMS message.
  */
sealed trait MsgConverter[D] {

  /**
    * Converts [[TextMessage]].
    *
    * @param msg JMS message to convert.
    * @return converted message.
    */
  def fromMessage(msg: TextMessage): D

  /**
    * Converts [[BytesMessage]].
    *
    * @param msg JMS message to convert.
    * @return converted message.
    */
  def fromMessage(msg: BytesMessage): D

  /**
    * Converts [[MapMessage]].
    *
    * @param msg JMS message to convert.
    * @return converted message.
    */
  def fromMessage(msg: MapMessage): D

  /**
    * Converts [[ObjectMessage]].
    *
    * @param msg JMS message to convert.
    * @return converted message.
    */
  def fromMessage(msg: ObjectMessage): D

  /**
    * Converts [[StreamMessage]].
    *
    * @param msg JMS message to convert.
    * @return converted message.
    */
  def fromMessage(msg: StreamMessage): D
}

/**
  * Converts JMS message to [[String]] object.
  *
  * @constructor Creates new Text converter.
  */
class TextConverter extends MsgConverter[String] {

  override def fromMessage(msg: TextMessage): String =
    msg.getText()

  override def fromMessage(msg: BytesMessage): String =
    throw new UnsupportedOperationException("Not supported")

  override def fromMessage(msg: MapMessage): String =
    throw new UnsupportedOperationException("Not supported")

  override def fromMessage(msg: ObjectMessage): String =
    throw new UnsupportedOperationException("Not supported")

  override def fromMessage(msg: StreamMessage): String =
    throw new UnsupportedOperationException("Not supported")
}
