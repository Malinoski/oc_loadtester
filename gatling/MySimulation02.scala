/**
 *  MySimulation02 v=0.1 
 *  @author Iuri Malinoski Teixeira
 *  Command line example:
 *  JAVA_OPTS="-Dusers=1 -Dramp=1 -DbaseUrl=http://localhost -DserverName=owncloudv2 -Duser=iuri -Dpassword=iuri" ./bin/gatling.sh -s MySimulation02 > out.txt && vim out.txt
 */

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.structure.ChainBuilder

class MySimulation02 extends Simulation {
  
  // My Parameters
  val nbUsers = Integer.getInteger("users", 1) // Second parameter is the default
  val myRamp = java.lang.Long.getLong("ramp", 1L) // Second parameter is the default
  val baseUrl = System.getProperty("baseUrl")
  val serverName = System.getProperty("serverName")
  val user = System.getProperty("user")
  val password = System.getProperty("password")  

  //My simulation
  val oc = OwnCloudSimulation
  var simulation: Iterator[ChainBuilder] = Iterator[ChainBuilder]();
  simulation = simulation ++ Iterator(oc.UploadFile("${requesttoken}.txt", "/"))
  simulation = simulation ++ Iterator(oc.UploadFile("${requesttoken}.txt", "/"))
  var i = 0
  for (i <- 1 to 2) {
    simulation = simulation ++ Iterator(oc.UploadFile("test-" + i + ".txt", "/"))
  }
  simulation = simulation ++ Iterator(oc.MoveFile("${requesttoken}.txt", "/", "/documents"))
  simulation = simulation ++ Iterator(oc.RenameFile("${requesttoken}.txt", "renamed-${requesttoken}.txt", "/documents"))
  simulation = simulation ++ Iterator(oc.DropFile("renamed-${requesttoken}.txt", "/documents"))
  for (i <- 1 to 2) {
    simulation = simulation ++ Iterator[ChainBuilder](oc.DropFile("test-" + i + ".txt", "/"))
  }
  
  //Build scenario
  val scenarion = OwnCloudSimulation(baseUrl, serverName, user, password,  simulation)
  
  //Run scenario
  setUp(scenarion.inject(rampUsers(nbUsers) over (myRamp seconds))).protocols(oc.httpProtocol)

}
