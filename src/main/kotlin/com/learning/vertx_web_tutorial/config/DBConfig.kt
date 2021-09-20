package com.learning.vertx_web_tutorial.config

import lombok.Value

@Value
class DBConfig {
  var host: String? = null
  var port: Int? = null
  var database: String? = null
  var user: String? = null
  var password: String? = null

  override fun toString(): String {
    return "DBConfig(host='$host', port=$port, database='$database', user='$user')"
  }
}
