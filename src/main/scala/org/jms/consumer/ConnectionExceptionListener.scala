package org.jms.consumer

import javax.jms.{ExceptionListener, JMSException}

class ConnectionExceptionListener extends ExceptionListener {

  override def onException(cause: JMSException) =
    throw new RuntimeException(cause)
}
