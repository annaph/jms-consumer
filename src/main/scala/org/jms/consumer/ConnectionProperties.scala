package org.jms.consumer

import java.util.Properties

sealed trait ConnectionProperties {
  def properties(): Properties
}

class TextConnectionProperties(brokerURL: String, topicName: String) extends ConnectionProperties {
  import TextConnectionProperties._

  def properties(): Properties = {
    val props = new Properties()

    props.put(BROKER_URL, brokerURL)
    props.put(TOPIC_NAME, topicName)

    props
  }
}

object TextConnectionProperties {
  val BROKER_URL = "brokerURL"
  val TOPIC_NAME = "topicName"
}
