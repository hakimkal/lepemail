package com.lepltd.web
package routes

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.lepltd.core.EmailService.{ErrorResponse, Response, SendEmailResponse}
import com.lepltd.core.EmailUtil.EmailHttpResponse
import com.lepltd.core.util.Enum.ResponseStatus
import com.lepltd.core.{EmailJsonProtocol, EmailService}
import org.slf4j.Logger
import spray.json.enrichAny

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class EmailRoutes(emailService: ActorRef[EmailService.Command])(implicit system: ActorSystem[_])
    extends EmailJsonProtocol {
  implicit val timeout: Timeout = 10.seconds
  lazy val log: Logger = system.log

  implicit val ec: ExecutionContextExecutor = system.executionContext

  lazy val emailServiceRoutes: Route = extractRequest { request =>
    path("send" / "email") {
      post {
        entity(as[EmailService.EmailModel]) { email =>


          log.info("{}",request.headers)
          val futRes   = emailService.ask((replyTo: ActorRef[Response])=>EmailService.SendEmailCommand(email, replyTo))

          onComplete(futRes) {
            case Success(result)  =>
              result match {
                case resp: SendEmailResponse=> complete(
                  EmailHttpResponse(ResponseStatus.Success,"Email Sent Successfully",Some(ResponseStatus.Success.id),Some(resp.toJson))

                )
                case resp: ErrorResponse=> complete(

                  EmailHttpResponse(ResponseStatus.BadRequest,"Failed",Some(ResponseStatus.BadRequest.id),Some(resp.toJson))
                )
              }

            case Failure(exception) =>
              failWith(exception)
          }
        }
      }
    }

  }

}