package com.lepltd.core

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.lepltd.core.EmailService.{EmailModel, ErrorResponse, Response, SendEmailResponse}
import com.lepltd.core.util.Enum.ResponseStatus
import spray.json.DefaultJsonProtocol

import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

  implicit val emailHttpResponse     = jsonFormat4(EmailHttpResponse)





}
