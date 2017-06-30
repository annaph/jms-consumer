
/*                     __                                               *\
**     ________ ___   / /  ___     jms-consumer                         **
**    / __/ __// _ | / /  / _ |    (c) 2017                             **
**  __\ \/ /__/ __ |/ /__/ __ |                                         **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package org.jms.consumer

import javax.jms.{ExceptionListener, JMSException}

/**
  * An exception listener that allows notification of a problem in case of
  * a serious problem related to the connection with an external JMS system.
  *
  * @constructor Creates new Connection exception listener.
  */
class ConnectionExceptionListener extends ExceptionListener {

  override def onException(cause: JMSException): Unit =
    throw new RuntimeException(cause)
}
