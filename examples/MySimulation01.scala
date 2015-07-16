//Command line example: $GATLING_HOME/bin/gatling.sh -s MySimulation03 > out.txt && vim out.txt

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.config.HttpProtocolBuilder.toHttpProtocol

class MySimulation01 extends Simulation {

  //Parameters
  val nbUsers = 1
  val myRamp: java.lang.Long = 1L
  val simulationName = "My Simulation 03"
  val baseUrl = "http://localhost"
  val serverName = "owncloudv2"
  val user = "iuri"
  val password = "iuri"

  //My Simulation 03
  var ocScenarioBuilder = new OCScenarioBuilder(simulationName, baseUrl, serverName, user, password)
  ocScenarioBuilder.addUploadFile("${requesttoken}.txt", "/")
  ocScenarioBuilder.addMoveFile("${requesttoken}.txt", "/", "/documents")
  ocScenarioBuilder.addDropFile("${requesttoken}.txt", "/documents")
  ocScenarioBuilder.addCreateFile("${requesttoken}-new", "txt", "/")
  ocScenarioBuilder.addRenameFile("${requesttoken}-new.txt", "${requesttoken}-renamed.txt", "/")
  ocScenarioBuilder.addDropFile("${requesttoken}-renamed.txt", "/")

  //Gatling simulation parameters
  var scenario = ocScenarioBuilder.getScenario()
  var httpProtocol = ocScenarioBuilder.getHttpProtocol()

  //Gatling run
  setUp(scenario.inject(rampUsers(nbUsers) over (myRamp seconds))).protocols(httpProtocol)

}