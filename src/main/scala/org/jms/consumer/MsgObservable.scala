package org.jms.consumer

import javax.jms.Message

import io.reactivex.Observable

trait MsgObservable {
  def messageObsevable(): Observable[Message]
}
