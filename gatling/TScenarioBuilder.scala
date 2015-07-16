import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.config.HttpProtocolBuilder

trait TScenarioBuilder extends Simulation {
  
  def setUploadFile(fileName: String, path: String);
  def run();
  
}