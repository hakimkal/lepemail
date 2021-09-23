package com.lepltd.core

import com.lepltd.core.util.Enum.ResponseStatus
import spray.json.JsValue

object EmailUtil {

  case class EmailHttpResponse(
    status: ResponseStatus.Value,
    description: String,
    code: Option[Int]     = None,
    data: Option[JsValue] = None)

}
