package com.lepltd
package core

import javax.mail.internet.InternetAddress
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success }

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, Behavior }
import courier._

import util.Config

object EmailService {
  case class EmailModel(toEmail: String, subject: String, body: String)

  sealed trait Command

  case class SendEmailCommand(email: EmailModel, replyTo: ActorRef[Response]) extends Command {

    def sendEmail(email: EmailModel): Future[Unit] = {
      println("About to send Email")
      val mailer =
        Mailer(Config.smtpHost, Config.smtpPort)
          .auth(true)
          .as(Config.smtpUser, Config.smtpPassword)
          .startTls(true)()

      val envelope = Envelope
        .from(new InternetAddress("abdulhakim@vdms.lepsms.ng"))
        .to(new InternetAddress(email.toEmail))
        .subject(email.subject)
        .content(Multipart().html(email.body))
      //.html("<html><body><h1>IT'S IMPORTANT</h1></body></html>")))
      mailer(envelope)

    }

  }

  sealed trait Response

  case class SendEmailResponse(email: String) extends Response

  case class ErrorResponse(message: String) extends Response

  def apply(): Behavior[Command] =
    Behaviors.receive({ (context, message) =>
      val log = context.log

      message match {

        case req: SendEmailCommand =>
          {

            req.sendEmail(req.email).onComplete {
              case Success(_) =>
                req.replyTo ! SendEmailResponse(req.email.toEmail)

              case Failure(ex) =>
                req.replyTo ! ErrorResponse("Error Occured  " + ex.getMessage)
            }

          }
          Behaviors.same
      }

    })

}
