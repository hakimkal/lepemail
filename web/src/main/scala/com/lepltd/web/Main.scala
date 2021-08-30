package com.lepltd.web


import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, PostStop}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.util.Timeout
import com.lepltd.core.EmailService

import com.lepltd.core.util.Config
import com.lepltd.web.routes.EmailRoutes

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object Main {

  sealed trait Message

  private final case class StartFailed(cause: Throwable) extends Message

  private final case class Started(binding: ServerBinding) extends Message

  case object Stop extends Message

  def apply(host: String, port: Int): Behavior[Message] =
    Behaviors.setup { ctx =>
      implicit val system = ctx.system



      implicit val timeout: Timeout = 5.seconds
      implicit val ec : ExecutionContextExecutor              = ctx.executionContext

      val emailService = ctx.spawn(EmailService(), "email-service")





      val route = new EmailRoutes(emailService)

      val serverBinding = Http().newServerAt(host, port).bind(route.emailServiceRoutes)

      ctx.pipeToSelf(serverBinding) {
        case Success(binding) =>
          Started(binding)
        case Failure(ex)      =>
          StartFailed(ex)
      }

      def running(binding: ServerBinding): Behavior[Message] =
        Behaviors
          .receiveMessagePartial[Message] { case Stop =>
            ctx.log.info(
              "Stopping server http://{}:{}",
              binding.localAddress.getHostString,
              binding.localAddress.getPort
            )
            Behaviors.stopped
          }
          .receiveSignal { case (_, PostStop) =>
            binding.unbind()
            Behaviors.same
          }

      def starting(wasStopped: Boolean): Behaviors.Receive[Message] =
        Behaviors.receiveMessage[Message] {
          case StartFailed(cause) =>
            throw new RuntimeException("Server failed to start", cause)
          case Started(binding)   =>
            ctx.log.info(
              "Server online at http://{}:{}",
              binding.localAddress.getHostString,
              binding.localAddress.getPort
            )

            if (wasStopped)
              ctx.self ! Stop
            running(binding)

          case Stop =>
            starting(wasStopped = true)
        }

      starting(wasStopped = false)
    }

  def main(args: Array[String]): Unit = {

    ActorSystem(Main.apply(Config.host, Config.port), "LepEmailServer")
  }

}
