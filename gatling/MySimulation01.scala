/**
 *  MySimulation01 v=0.1 
 *  @author Iuri Malinoski Teixeira
 *  Command line example:
 *  JAVA_OPTS="-Dusers=1 -Dramp=1 -DbaseUrl=http://localhost -DserverName=owncloudv2 -Duser=iuri -Dpassword=iuri" ./bin/gatling.sh -s MySimulation01 > out.txt && vim out.txt
 */

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.structure.ChainBuilder

class MySimulation01 extends Simulation {
  
  // My Parameters
  val nbUsers = Integer.getInteger("users", 1) // Second parameter is the default
  val myRamp = java.lang.Long.getLong("ramp", 1L) // Second parameter is the default
  val baseUrl = System.getProperty("baseUrl")
  val serverName = System.getProperty("serverName")
  val user = System.getProperty("user")
  val password = System.getProperty("password")  

  //My simulation
  var simulation: Iterator[ChainBuilder] = Iterator[ChainBuilder]();
  simulation = simulation ++ Iterator(OwnCloudSimulation.UploadFile("${requesttoken}.txt", "/documents"))
  simulation = simulation ++ Iterator(OwnCloudSimulation.DropFile("${requesttoken}.txt", "/documents"))
  
  //Build scenario
  var scenarion = OwnCloudSimulation(baseUrl, serverName, user, password,  simulation)
  
  //Run scenario
  setUp(scenarion.inject(rampUsers(nbUsers) over (myRamp seconds))).protocols(OwnCloudSimulation.httpProtocol)

}
