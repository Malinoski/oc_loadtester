//Command line example: $GATLING_HOME/bin/gatling.sh -s MySimulation > out.txt && vim out.txt

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.config.HttpProtocolBuilder.toHttpProtocol

class MySimulation extends Simulation {

  //Parameters
  val nbUsers = 5
  val myRamp: java.lang.Long = 5L
  val simulationName = "My Simulation"
  val baseUrl = "http://localhost" 
  val serverName = "owncloud-8.1.0"
  //val serverName = "owncloud-8.0.3"
  //val serverName = "owncloud-7.0.4"
  //val serverName = "owncloud-5.0.18"
  val user = "testuser"
  val password = "testuser"

  //My Simulation (note: the "Documents" folder must exist)
  var builder: TScenarioBuilder = new OCScenarioBuilder(simulationName, baseUrl, serverName, user, password)
  builder.addUploadFile("${userId}.txt", "/")
  builder.addMoveFile("${userId}.txt", "/", "/Documents")
  builder.addDropFile("${userId}.txt", "/Documents")
  builder.addCreateFile("${userId}-new", "txt", "/")
  builder.addRenameFile("${userId}-new.txt", "${userId}-renamed.txt", "/")
  builder.addDropFile("${userId}-renamed.txt", "/")

  //Gatling simulation parameters
  var scenario = builder.getScenario() 
  var httpProtocol = builder.getHttpProtocol() 

  //Gatling run
  setUp(scenario.inject(rampUsers(nbUsers) over (myRamp seconds))).protocols(httpProtocol)

}