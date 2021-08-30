package com.lepltd
package core.util

import com.typesafe.config.{ConfigValue, ConfigFactory}

object Config {
  lazy val config = ConfigFactory.load()
  config.checkValid(ConfigFactory.defaultReference)

  val host: String = config.getString("lepemail.app.host")

  val port: Int = config.getInt("lepemail.app.port")

  val smtpHost: String = config.getString("lepemail.app.server")
  val smtpPort: Int = config.getInt("lepemail.app.emailServerPort")
  val smtpUser: String = config.getString("lepemail.app.user")
  val smtpPassword: String = config.getString("lepemail.app.password")
  val apiUserKeys: Seq[String] = config.getString("lepemail.app.apiKeys").split(",").toSeq


}

