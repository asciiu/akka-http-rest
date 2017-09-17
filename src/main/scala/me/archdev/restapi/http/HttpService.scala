package me.archdev.restapi.http

import akka.http.scaladsl.model.StatusCodes.Found
import akka.http.scaladsl.server.Directives._
import com.softwaremill.session.{InMemoryRefreshTokenStorage, SessionConfig, SessionManager}
import me.archdev.restapi.http.routes.{AuthServiceRoute, UsersServiceRoute}
import me.archdev.restapi.models.MyScalaSession
import me.archdev.restapi.services.{AuthService, UsersService}
import me.archdev.restapi.utils.CorsSupport
import com.softwaremill.session.CsrfDirectives._
import com.softwaremill.session.CsrfOptions._
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.SessionOptions._
import com.softwaremill.session._
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext

class HttpService(usersService: UsersService,
                  authService: AuthService
                 )(implicit executionContext: ExecutionContext) extends CorsSupport with StrictLogging {

  val usersRouter = new UsersServiceRoute(authService, usersService)
  val authRouter = new AuthServiceRoute(authService)

  val sessionConfig = SessionConfig.default("c05ll3lesrinf39t7mc5h6un6r0c69lgfno69dsak3vabeqamouq4328cuaekros401ajdpkh60rrtpd8ro24rbuqmgtnd1ebag6ljnb65i8a55d482ok7o0nch0bfbe")
  implicit val sessionManager = new SessionManager[MyScalaSession](sessionConfig)
  implicit val refreshTokenStorage = new InMemoryRefreshTokenStorage[MyScalaSession] {
    def log(msg: String) = logger.info(msg)
  }

  def mySetSession(v: MyScalaSession) = setSession(refreshable, usingHeaders, v)

  val myRequiredSession = requiredSession(refreshable, usingHeaders)
  val myInvalidateSession = invalidateSession(refreshable, usingHeaders)

  val routes =
    pathPrefix("api") {
      path("do_login") {
        post {
          entity(as[String]) { body =>
            logger.info(s"Logging in $body")

            mySetSession(MyScalaSession(body)) { ctx =>
              //setNewCsrfToken(checkHeader) { ctx => ctx.complete("ok") }
              ctx.complete("ok")
            }
          }
        }
      } ~
      // This should be protected and accessible only when logged in
      path("do_logout") {
              post {
                myRequiredSession { session =>
                  myInvalidateSession { ctx =>
                    logger.info(s"Logging out $session")
                    ctx.complete("ok")
                  }
                }
              }
            } ~
      // This should be protected and accessible only when logged in
      path("current_login") {
        get {
          myRequiredSession { session => ctx =>
            logger.info("Current session: " + session)
            ctx.complete(session.username)
          }
        }
      }

        //corsHandler {
        //
        //usersRouter.route ~
        //authRouter.route
      //}
    }

}
