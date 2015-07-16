import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.config.HttpProtocolBuilder

class OCScenarioBuilder (simulationName: String, baseUrl: String, serverName: String, user: String, password: String){

  private var chainBuilderIterator: Iterator[ChainBuilder] = Iterator[ChainBuilder]();
  chainBuilderIterator = chainBuilderIterator ++ Iterator[ChainBuilder](ownCloudSimulation.Token())
  chainBuilderIterator = chainBuilderIterator ++ Iterator[ChainBuilder](ownCloudSimulation.Login(user, password))
  
  private var ownCloudSimulation = OwnCloudSimulation (simulationName, baseUrl, serverName, user, password)

  //Upload
  def addUploadFile(fileName: String, path: String) {
    chainBuilderIterator = chainBuilderIterator ++ Iterator(ownCloudSimulation.UploadFile(fileName, path))
  }
  
  //Move
  def addMoveFile (fileName: String, origin: String, destination: String) {
    chainBuilderIterator = chainBuilderIterator ++ Iterator(ownCloudSimulation.MoveFile(fileName, origin, destination))      
  }
  
  //create
  def addCreateFile (fileName: String, fileExtension: String, path: String) {
      chainBuilderIterator = chainBuilderIterator ++ Iterator(ownCloudSimulation.CreateFile(fileName, fileExtension, path))
  }
  
  //drop
  def addDropFile (fileName: String, path: String) {
      chainBuilderIterator = chainBuilderIterator ++ Iterator(ownCloudSimulation.DropFile(fileName, path))
  }  
  
  //rename
  def addRenameFile (oldFileName: String, newFileName: String, path: String) {
      chainBuilderIterator = chainBuilderIterator ++ Iterator(ownCloudSimulation.RenameFile(oldFileName, newFileName, path))
  }
  
  def getScenario() = {
    var it: Iterator[ChainBuilder] = Iterator[ChainBuilder]();
    it = it ++ chainBuilderIterator
    it = it ++ Iterator[ChainBuilder](ownCloudSimulation.Logout())
    scenario(simulationName).exec(it)
  }
  
  def getHttpProtocol() = {
    ownCloudSimulation.httpProtocol
  }

}