package org.jms.consumer

import javax.jms.Message

import io.reactivex.Flowable

trait MsgFlowable {
  def messageFlowable(): Flowable[Message]
}

class TextFlowable extends MsgFlowable {
  def messageFlowable(): Flowable[Message] = ???
}
