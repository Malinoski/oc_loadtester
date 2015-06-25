/**  
 *  @author Iuri Malinoski Teixeira   
 *  Example to use:
 *  sudo JAVA_OPTS="-Dusers=1 -Dramp=1 -Dhost=localhost -DserverName=owncloud -Duser=iuri -Dpassword=iuri" ./bin/gatling.sh -s OwnCloudSimulation > out.txt && vim out.txt
 */

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class OwnCloudSimulation extends Simulation {

  val nbUsers = Integer.getInteger("users", 1) // Second parameter is the default
  val myRamp = java.lang.Long.getLong("ramp", 1L) // Second parameter is the default
  val host = System.getProperty("host")
  val baseUrl = "http://" + host
  val serverName = System.getProperty("serverName")
  val user = System.getProperty("user")
  val password = System.getProperty("password")

  val httpProtocol = http
    .baseURL(baseUrl)
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .connection("keep-alive")
    .contentTypeHeader("application/x-www-form-urlencoded; charset=UTF-8")
    .userAgentHeader("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:38.0) Gecko/20100101 Firefox/38.0")

  var fileTestExtension = ".txt";

  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

  val headers_1 = Map(
    "Pragma" -> "no-cache",
    "X-Requested-With" -> "XMLHttpRequest")

  val headers_2 = Map(
    "X-Requested-With" -> "XMLHttpRequest")

  object Token {
    //Sample usage (note requesttoken attribute)
    //.get(uri1 + "/index.php/avatar/iuri/32?requesttoken=${requesttoken}")    
    val token =
      exec(http("request_token")
        .get("/" + serverName + "/index.php")
        .headers(headers_0)
        .check(regex("""<head data-requesttoken="([^"]*)">""").saveAs("requesttoken"))) // ex.: <head data-requesttoken="80ffb462c13a0f451a5b">      
  }

  object Login {
    val login =
      exec(http("request_70")
        .post("/" + serverName + "/index.php")
        .headers(headers_0)
        .formParam("user", user)
        .formParam("password", password)
        .formParam("timezone-offset", "-3")
        .header("requesttoken", "${requesttoken}"))
  }

  object CreateFile {

    val createFile =
      exec(http("request_CreateFile")
        .post("/" + serverName + "/index.php/apps/files/ajax/newfile.php")
        .headers(headers_0)
        .header("requesttoken", "${requesttoken}") // Included
        .formParam("dir", "/")
        .formParam("filename", "${requesttoken}" + fileTestExtension))
  }

  object Logout {
    val logout =
      exec(http("request_96")
        .get("/" + serverName + "/index.php?logout=true&requesttoken=${requesttoken}")
        .headers(headers_0))
  }

  object DropFile {

    val dropFile =
      exec(http("request_1_drop")
        .post("/" + serverName + "/index.php/apps/files/ajax/delete.php")
        .headers(headers_1)
        .header("requesttoken", "${requesttoken}")
        .formParam("dir", "/documents")
        .formParam("files", """["""" + "${requesttoken}-2" + fileTestExtension + """"]"""))
  }

  object UploadFile {

    val headers_0 = Map(
      "Pragma" -> "no-cache",
      "X-Requested-With" -> "XMLHttpRequest",
      //"Content-Type" -> "multipart/form-data; boundary=---------------------------6884128904720638791748211959")
      "Content-Type" -> "multipart/form-data; boundary=stringToBeUsedByServerToParseTheFile")

    var content = """
--stringToBeUsedByServerToParseTheFile
Content-Disposition: form-data; name="requesttoken"

""" + "${requesttoken}" + """
--stringToBeUsedByServerToParseTheFile
Content-Disposition: form-data; name="dir"

/
--stringToBeUsedByServerToParseTheFile
Content-Disposition: form-data; name="file_directory"


--stringToBeUsedByServerToParseTheFile
Content-Disposition: form-data; name="files[]"; filename="
""" + "${requesttoken}" + fileTestExtension + """
"
Content-Type: text/plain

some
text
--stringToBeUsedByServerToParseTheFile--""";

    val uploadFile =
      exec(http("request_0_upload")
        .post("/" + serverName + "/index.php/apps/files/ajax/upload.php")
        .headers(headers_0)
        .body(StringBody(content)) //.check(status.not(200), status.is(200)) // for debug
        )
  }

  object MoveFile {

    val moveFile =
      exec(http("request_1_move")
        .post("/" + serverName + "/index.php/apps/files/ajax/move.php")
        .headers(headers_1)
        .header("requesttoken", "${requesttoken}")
        .formParam("dir", "/")
        .formParam("file", "${requesttoken}" + fileTestExtension) // obs.: ${requesttoken} is the file name
        .formParam("target", "//documents"))
  }

  object RenameFile {

    val renameFile =
      exec(http("request_0_rename")
        .get("/" + serverName + "/index.php/apps/files/ajax/rename.php?dir=%2Fdocuments&newname=${requesttoken}-2" + fileTestExtension + "&file=${requesttoken}" + fileTestExtension)
        .headers(headers_2)
        .header("requesttoken", "${requesttoken}"))
  }

  val scn = scenario("Scenario Name").exec(
    Token.token,
    Login.login,
    //CreateFile.createFile, // Create a file in "/". The file name is based on token ([token].txt, ex.: bf579ddbc0a4cfe5c802.txt)
    UploadFile.uploadFile, // Upload a file to "/". The file name is the same for CreateFile
    MoveFile.moveFile, // Move the file to "/Documents"
    RenameFile.renameFile, // Rename the file [token] to [token]new in "/documents"
    DropFile.dropFile, // Drop the file in "/documents"
    Logout.logout)

  setUp(scn.inject(rampUsers(nbUsers) over (myRamp seconds))).protocols(httpProtocol)
}