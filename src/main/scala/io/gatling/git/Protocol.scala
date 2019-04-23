package io.gatling.git

import io.gatling.commons.stats.Status
import io.gatling.commons.util.DefaultClock
import io.gatling.core.CoreComponents
import io.gatling.core.action.{Action, ChainableAction, ExitableAction}
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.protocol.Protocol
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.util.NameGen
import io.gatling.core.session.Session
import io.gatling.commons.stats.{OK => GatlingOK}
import io.gatling.commons.stats.{KO => GatlingFail}

case class GitProtocol(server: String = "localhost",
                       httpPort: Int = 8080,
                       sshPort: Int = 29418)
    extends Protocol {

  def call(command: Command): Response = {
    val response = command.run
    response
  }

}

class GitCall(coreComponents: CoreComponents,
              command: Command,
              protocol: GitProtocol,
              val next: Action)
    extends ChainableAction
    with ExitableAction
    with NameGen {

  val statsEngine = coreComponents.statsEngine
  val clock = new DefaultClock
  override def name: String = genName(command.getClass.toString)

  override def execute(session: Session): Unit = {
    val start = clock.nowMillis

    val response = protocol.call(command)
    statsEngine.logResponse(session,
                            command.toString,
                            start,
                            clock.nowMillis,
                            gatlingStatusFromGit(response),
                            None,
                            None)
    next ! session.markAsSucceeded
  }

  private def gatlingStatusFromGit(response: Response): Status = {
    response.status match {
      case OK   => GatlingOK
      case Fail => GatlingFail
    }
  }
}

case class GitProtocolBuilder(gitProtocol: GitProtocol) {
  def build = gitProtocol
}

class GitActionBuilder(command: Command) extends ActionBuilder {
  //XXX Pass Server in builder
  override def build(ctx: ScenarioContext, next: Action): Action = {
    import ctx.coreComponents
    new GitCall(coreComponents, command, GitProtocol("localhost"), next)
  }
}

case class Response(status: ResponseStatus)

sealed trait ResponseStatus
case object OK extends ResponseStatus
case object Fail extends ResponseStatus
