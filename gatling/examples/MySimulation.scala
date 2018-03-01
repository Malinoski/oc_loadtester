// Ex.: JAVA_OPTS="-Dramp=10 -Dusers=100 -Durl=http://10.40.0.2:84" $GATLING_HOME/bin/gatling.sh -s MySimulation -rf /path/to/store/the/results; 

$GATLING_HOME/bin/gatling.sh -s MySimulation  

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.config.HttpProtocolBuilder.toHttpProtocol

class MySimulation extends Simulation {


  //Parameters
  //val myRamp: java.lang.Long = 10L
  //val nbUsers = 500

  val nbUsers = Integer.getInteger("users", 1)
  val myRamp  = java.lang.Long.getLong("ramp", 0L)
  val simulationName = "My Simulation"
  val baseUrl = System.getProperty("url")
  
  //val baseUrl = "http://10.40.0.2:84" // CONTAINER
  //val baseUrl = "http://146.134.226.151" // VM
  
  val serverName = "owncloud"
  val user = "admin"
  val password = "admin"

  // println("Ramp: " + myRamp)
  // println("Users: " + nbUsers)
  // println("Url: " + baseUrl) 

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