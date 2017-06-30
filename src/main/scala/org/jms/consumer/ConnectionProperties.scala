
/*                     __                                               *\
**     ________ ___   / /  ___     jms-consumer                         **
**    / __/ __// _ | / /  / _ |    (c) 2017                             **
**  __\ \/ /__/ __ |/ /__/ __ |                                         **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package org.jms.consumer

/**
  * Placeholder for properties required to establish connection with external JMS system.
  *
  * Following objects use this trait:
  * [[TextConnectionProperties]]
  *
  */
sealed trait ConnectionProperties {

  /**
    * Returns connection properties as a map where key is property name and value is property value.
    *
    * @return connection properties.
    */
  def properties(): Map[String, String]
}

/**
  * Connection properties required to establish connection with Apache ActiveMQ JMS.
  *
  * @constructor Creates new Text connection properties.
  * @param brokerURL Apache Active MQ broker URL.
  * @param topicName name of the topic to subscribe to.
  */
class TextConnectionProperties(brokerURL: String, topicName: String) extends ConnectionProperties {

  import TextConnectionProperties._

  override def properties(): Map[String, String] =
    Map(
      BROKER_URL -> brokerURL,
      TOPIC_NAME -> topicName)
}

/** Factory for [[TextConnectionProperties]] instances. */
object TextConnectionProperties {
  val BROKER_URL = "brokerURL"
  val TOPIC_NAME = "topicName"
}
