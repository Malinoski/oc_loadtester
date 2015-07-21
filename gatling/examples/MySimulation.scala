//Command line example: JAVA_OPTS="-DbaseUrl=http://localhost" $GATLING_HOME/bin/gatling.sh -s MySimulation > out.txt && vim out.txt

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.config.HttpProtocolBuilder.toHttpProtocol

class MySimulation extends Simulation {

  //Parameters
  val nbUsers = 10
  val myRamp: java.lang.Long = 5L
  val simulationName = "My Simulation 03"
  val baseUrl = System.getProperty("baseUrl") 
  val serverName = "owncloud-8.0.3" //owncloud 8
  //val serverName = "owncloudv2" // owncloud 7
  val user = "iuri"
  val password = "iuri"

  //My Simulation 03
  var builder: TScenarioBuilder = new OCScenarioBuilder(simulationName, baseUrl, serverName, user, password)
  builder.addUploadFile("${userId}.txt", "/")
  builder.addMoveFile("${userId}.txt", "/", "/documents")
  builder.addDropFile("${userId}.txt", "/documents")
  builder.addCreateFile("${userId}-new", "txt", "/")
  builder.addRenameFile("${userId}-new.txt", "${userId}-renamed.txt", "/")
  builder.addDropFile("${userId}-renamed.txt", "/")

  //Gatling simulation parameters
  var scenario = builder.getScenario() 
  var httpProtocol = builder.getHttpProtocol() 

  //Gatling run
  setUp(scenario.inject(rampUsers(nbUsers) over (myRamp seconds))).protocols(httpProtocol)

}