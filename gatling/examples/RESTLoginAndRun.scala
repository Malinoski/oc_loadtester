/**
 *  @author Iuri Malinoski Teixeira
 *
 *  How to Deploy:
 *  This code must be in sinapad folder, ex.: $GATLING_HOME/user-files/simulations/sinapad/RESTLoginAndRun.scala
 *
 *  Example to use:
 *  JAVA_OPTS="-Dbaseurl=http://my.rest.server:1234 -Dusername=myrestuser -Dpassword=mysecretepassword -Dusers=1" $GATLING_HOME/bin/gatling.sh -s sinapad.RESTLoginAndRun
 */

package sinapad

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RESTLoginAndRun extends Simulation {

        val baseUrl     = System.getProperty("baseurl")
        val username    = System.getProperty("username")
        val password    = System.getProperty("password")
        val users       = Integer.getInteger("users", 1)

        val httpProtocol = http
                .baseURL(baseUrl)
                .inferHtmlResources()
                .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")

        object Uid {

                val headers_0 = Map("Upgrade-Insecure-Requests" -> "1")

                val headers_1 = Map(
                        "Accept" -> "application/json, text/plain, */*",
                        "Accept-Encoding" -> "gzip, deflate",
                        "Accept-Language" -> "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7",
                        "Connection" -> "keep-alive",
                        "Origin" -> baseUrl,
                        "X-Requested-With" -> "XMLHttpRequest")

                var uid =
                        exec(http("request_0")
                                .get("/rest/test-job-submission-run.jsp")
                                .headers(headers_0))
                        .exec(http("request_1")
                                .post("/rest/op/authentication/login-ldap")
                                .headers(headers_1)
                                .formParam("username", username)
                                .formParam("password", password)
                                .formParam("service", "CSGrid")
                                .check(regex("""uuid":"([^"]*)"""")saveAs("uidtoken"))
                                // {"type":"authenticationResult","code":200,"uuid":"9f394e06-9182-42a3-8360-3efdb0d3717d"}
                        )
        }
        object Run {

                val headers_2 = Map(
                        "Cache-Control" -> "max-age=0",
                        "Origin" -> baseUrl,
                        "Upgrade-Insecure-Requests" -> "1")

                var run =
                        exec(http("request_2")
                                .post("/rest/op/job-submission/run")
                                .headers(headers_2)
                                .formParam("uuid", "${uidtoken}")
                                .formParam("service", "CSGrid")
                                .formParam("project", "LoadTest")
                                .formParam("application", "Contar caracteres OSC")
                                .formParam("version", "1.0.0")
                                .formParam("args", "INPUT::/caracteres.txt;OUTPUT::/result.txt")
                                .formParam("extras", ""))

        }

        val scn = scenario("Scenario Name").exec(Uid.uid, Run.run)

        setUp(scn.inject(atOnceUsers(users))).protocols(httpProtocol)
}
