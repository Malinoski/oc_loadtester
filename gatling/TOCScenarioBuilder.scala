import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.config.HttpProtocolBuilder
import io.gatling.core.structure.ScenarioBuilder 

trait TScenarioBuilder {
  
  var chainBuilderIterator: Iterator[ChainBuilder] = Iterator[ChainBuilder]();
  var ownCloudSimulation = OwnCloudSimulation;
  
  //Upload
  def addUploadFile(fileName: String, path: String);
  
  //Move
  def addMoveFile (fileName: String, origin: String, destination: String);
  
  //create
  def addCreateFile (fileName: String, fileExtension: String, path: String);
  
  //drop
  def addDropFile (fileName: String, path: String);
  
  //rename
  def addRenameFile (oldFileName: String, newFileName: String, path: String);
  
  def getScenario(): ScenarioBuilder;
  
  def getHttpProtocol(): HttpProtocolBuilder;
  
  //def getToken(): String;
  
}