package org.jms.consumer

sealed trait ConnectionProperties {
  def properties(): Map[String, String]
}

class TextConnectionProperties(brokerURL: String, topicName: String) extends ConnectionProperties {

  import TextConnectionProperties._

  def properties(): Map[String, String] =
    Map(
      BROKER_URL -> brokerURL,
      TOPIC_NAME -> topicName)
}

object TextConnectionProperties {
  val BROKER_URL = "brokerURL"
  val TOPIC_NAME = "topicName"
}
