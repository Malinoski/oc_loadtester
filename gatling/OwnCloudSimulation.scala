/**
 *  Version: 0.8
 *  @author Iuri Malinoski Teixeira
 *  Example to use:
 *  JAVA_OPTS="-Dusers=1 -Dramp=1 -Dhost=localhost -DserverName=owncloudv2 -Duser=iuri -Dpassword=iuri" ./bin/gatling.sh -s OwnCloudSimulation > out.txt && vim out.txt
 */

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.config.HttpProtocolBuilder
import io.gatling.http.check.HttpCheck

object OwnCloudSimulation {

  var baseUrl: String = ""
  var serverName: String = ""
  var user: String = ""
  var password: String = ""
  var httpProtocol: HttpProtocolBuilder = HttpProtocolBuilder.DefaultHttpProtocolBuilder

  var headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

  var headers_1 = Map(
    "OCS-APIREQUEST" -> "true",
    "Pragma" -> "no-cache",
    "X-Requested-With" -> "XMLHttpRequest")

  var headers_2 = Map(
    "X-Requested-With" -> "XMLHttpRequest")
    
  object setSessionUserId  {
    def apply() = {
      exec(session => session.set("userId", session.userId))
    }  
  }
  
  object Token {
    def apply() =
      exec(http("request_token")
        .get("/" + serverName + "/index.php")
        .headers(headers_0)
        //.check(proccessRegex()))
        //.check(proccessRegex()saveAs("requesttoken"))) // ex.: <head data-requesttoken="80ffb462c13a0f451a5b">
        .check(regex("""<head data-requesttoken="([^"]*)">""")saveAs("requesttoken"))) // ex.: <head data-requesttoken="80ffb462c13a0f451a5b">        
        //.check(regex("""<head data-requesttoken="([a-zA-Z0-9]*)">""").saveAs("requesttoken"))) // ex.: <head data-requesttoken="80ffb462c13a0f451a5b">
        //.check(regex("""<head data-requesttoken="^[a-zA-Z]+$">""").saveAs("requesttoken"))) // ex.: <head data-requesttoken="80ffb462c13a0f451a5b">
  }

  object Login {

    def apply(user: String, password: String) =
      exec(http("request_70")
        .post("/" + serverName + "/index.php")
        .headers(headers_0)
        .formParam("user", user)
        .formParam("password", password)
        .formParam("timezone-offset", "-3")
        .header("requesttoken", "${requesttoken}"))
  }

  object Logout {

    def apply() =
      exec(http("request_96")
        .get("/" + serverName + "/index.php?logout=true&requesttoken=${requesttoken}")
        .headers(headers_0))
  }

  object UploadFile {

    val headers_0 = Map(
      "Pragma" -> "no-cache",
      "X-Requested-With" -> "XMLHttpRequest",
      "Content-Type" -> "multipart/form-data; boundary=stringToBeUsedByServerToParseTheFile")

    var token = "${requesttoken}"
      
    def apply(fileName: String, path: String) =
      //exec(session => session.set("userId", session.userId))
      //exec(setSessionUserId())
      exec(http("request_0_upload")
        .post("/" + serverName + "/index.php/apps/files/ajax/upload.php")
        .headers(headers_0)
        .body(StringBody(
          """
--stringToBeUsedByServerToParseTheFile
Content-Disposition: form-data; name="requesttoken"

""" + token + """
--stringToBeUsedByServerToParseTheFile
Content-Disposition: form-data; name="dir"

""" + path + """
--stringToBeUsedByServerToParseTheFile
Content-Disposition: form-data; name="file_directory"


--stringToBeUsedByServerToParseTheFile
Content-Disposition: form-data; name="files[]"; filename="""" + fileName + """
Content-Type: text/plain

some text

--stringToBeUsedByServerToParseTheFile--
""")) //.check(status.not(200), status.is(200)) // for debug
)
  }

  object MoveFile {

    def apply(fileName: String, origin: String, destination: String) =
      exec(http("request_move")
        .post("/" + serverName + "/index.php/apps/files/ajax/move.php")
        .headers(headers_1)
        .header("requesttoken", "${requesttoken}")
        .formParam("dir", "/")
        .formParam("file", fileName)
        .formParam("target", destination))
  }

  object RenameFile {

    def apply(oldFileName: String, newFileName: String, path: String) =
      exec(http("request_rename")
        .get("/" + serverName + "/index.php/apps/files/ajax/rename.php?dir=" + path + "&newname=" + newFileName + "&file=" + oldFileName)
        .headers(headers_2)
        .header("requesttoken", "${requesttoken}"))
  }

  object CreateFile {

    def apply(fileName: String, fileExtension: String, path: String) =
      exec(http("request_CreateFile")
        .post("/" + serverName + "/index.php/apps/files/ajax/newfile.php")
        .headers(headers_0)
        .header("requesttoken", "${requesttoken}") // Included
        .formParam("dir", path)
        //.formParam("filename", fileTest)
        .formParam("filename", fileName + "." + fileExtension))
  }

  object DropFile {

    def apply(fileName: String, path: String) =
      exec(http("request_1_drop")
        .post("/" + serverName + "/index.php/apps/files/ajax/delete.php")
        .headers(headers_1)
        .header("requesttoken", "${requesttoken}")
        .formParam("dir", path)
        .formParam("files", """["""" + fileName + """"]"""))
  }
  
  def apply(simulationName: String, baseUrl: String, serverName: String, user: String, password: String) = {

    this.baseUrl = baseUrl
    this.serverName = serverName
    this.user = user
    this.password = password
    this.httpProtocol = http
    .baseURL(baseUrl)
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .connection("keep-alive")
    .contentTypeHeader("application/x-www-form-urlencoded; charset=UTF-8")
    .userAgentHeader("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:38.0) Gecko/20100101 Firefox/38.0")
  //    //For debug:
  //    .extraInfoExtractor(extraInfo => List(
  //      "</br>##### REQUEST #####</br>" + extraInfo.request +
  //        "</br>##### RESPONSE #####</br>" + extraInfo.response + "</br></br>"))
    this
  }
  
}
