package gerrit

import io.gatling.git.{Clone, GitActionBuilder, GitProtocol}
import io.gatling.core.Predef.{exec, _}
import io.gatling.core.structure.ScenarioBuilder

class BasicSimulation extends Simulation {

  val gitProtocol = GitProtocol()

  val scenario1: ScenarioBuilder = scenario("Git Clone").repeat(50)(
    exec(new GitActionBuilder(Clone("http://localhost:8081/test")))
      .exec(new GitActionBuilder(Clone("http://localhost:8081/test2")))
  )

  setUp(scenario1.inject(atOnceUsers(3)))
    .protocols(gitProtocol)
}
