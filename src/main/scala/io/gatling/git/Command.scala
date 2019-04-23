package io.gatling.git

import java.io.File

import org.eclipse.jgit.api.CloneCommand

trait Command {
  def run: Response
}

case class Clone(repoURI: String) extends Command {
  //XXX repoURI should be build using port and URL coming from Protocol
  override def run: Response = {
    val rnd = Math.random()
    val cloneCommand = new CloneCommand()
      .setURI(repoURI)
      .setDirectory(new File(s"/tmp/test-$rnd"))
    try {
      cloneCommand.call()
      Response(OK)
    } catch {
      case _: Throwable => {
        Response(Fail)
      }
    }
  }
}
