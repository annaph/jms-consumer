package org.jms.consumer

import javax.jms.{Message, TextMessage}

import org.springframework.stereotype.Service

@Service
class MsgConverter {

  def fromMessage(msg: TextMessage): String =
    msg.getText()

  def fromMessage(msg: Message): String =
    throw new Exception("Not supported")
}
