package com.learning.vertx_web_tutorial.db

import com.learning.vertx_web_tutorial.config.BrokerConfig
import io.vertx.core.Vertx
import io.vertx.mysqlclient.MySQLAuthOptions
import io.vertx.mysqlclient.MySQLConnectOptions
import io.vertx.mysqlclient.MySQLPool
import io.vertx.pgclient.PgConnectOptions
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.PoolOptions

class DBPools {
  companion object {

    fun createPgPool(config: BrokerConfig, vertx: Vertx): Pool {
      val connectOptions = PgConnectOptions()
      connectOptions.host = config.dbConfig!!.host
      connectOptions.port = config.dbConfig!!.port!!.toInt()
      connectOptions.database = config.dbConfig!!.database
      connectOptions.user = config.dbConfig!!.user
      connectOptions.password = config.dbConfig!!.password

      val poolOptions = PoolOptions()
      poolOptions.maxSize = 4

      return PgPool.pool(vertx, connectOptions, poolOptions)
    }

    fun createMySQLPool(config: BrokerConfig, vertx: Vertx): Pool {
      val connectOptions = MySQLConnectOptions()
      connectOptions.host = config.dbConfig!!.host
      connectOptions.port = config.dbConfig!!.port!!.toInt()
      connectOptions.database = config.dbConfig!!.database
      connectOptions.user = config.dbConfig!!.user
      connectOptions.password = config.dbConfig!!.password

      val poolOptions = PoolOptions()
      poolOptions.maxSize = 4

      return MySQLPool.pool(vertx, connectOptions, poolOptions)
    }
  }
}
