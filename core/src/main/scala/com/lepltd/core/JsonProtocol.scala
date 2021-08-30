package com.lepltd.core

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.lepltd.core.EmailService.{ EmailModel, ErrorResponse, Response, SendEmailResponse }
import com.lepltd.core.util.Enum.ResponseStatus
import spray.json.DefaultJsonProtocol

trait EmailJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  // import the default encoders for primitive types (Int, String, Lists etc)

  import com.lepltd.core.util.Enum.ResponseStatus._
  import EmailUtil._
  import spray.json._

  implicit val emailModel    = jsonFormat3(EmailModel)
  implicit val errorResponse = jsonFormat1(ErrorResponse)
  implicit val sendEmailResponse = jsonFormat1(SendEmailResponse)



  implicit object ResponseStatusFormat extends RootJsonFormat[ResponseStatus.Value] {

    def write(status: ResponseStatus.Value): JsValue =
      status match {
        case BadRequest       =>
          JsString("BadRequest")
        case Success          =>
          JsString("Success")
        case Created          =>
          JsString("Created")
        case NotFound         =>
          JsString("NotFound")
        case DuplicateRequest =>
          JsString("DuplicateRequest")
      }

    def read(json: JsValue): ResponseStatus.Value =
      json match {
        case JsString("BadRequest")       =>
          BadRequest
        case JsString("Success")          =>
          Success
        case JsString("Created")          =>
          Created
        case JsString("NotFound")         =>
          NotFound
        case JsString("DuplicateRequest") =>
          DuplicateRequest
        case _                            =>
          throw new DeserializationException("Status unexpected")
      }

  }

  implicit val httpResponse = jsonFormat4(EmailHttpResponse)

  implicit object EmailServiceResponseJsonFormat extends RootJsonFormat[EmailService.Response] {

    def write(a: Response) =
      a match {

        case e: ErrorResponse     =>
          e.toJson

      }

    def read(value: JsValue) =
      // If you need to read, you will need something in the
      // JSON that will tell you which subclass to use
      value.asJsObject.fields("") match {
        case JsString("errorResponse") =>
          value.convertTo[ErrorResponse]

      }

  }

}
