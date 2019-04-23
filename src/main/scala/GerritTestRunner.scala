import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

object GerritTestsRunner {

  def main(args: Array[String]): Unit = {

    val props = new GatlingPropertiesBuilder
    props.runDescription("test")

    Gatling.fromMap(props.build)
  }

}
