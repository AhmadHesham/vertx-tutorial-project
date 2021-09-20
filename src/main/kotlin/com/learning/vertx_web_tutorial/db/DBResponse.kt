package com.learning.vertx_web_tutorial.db

import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

class DBResponse {

  companion object {
    private val LOGGER = LoggerFactory.getLogger(DBResponse::class.java)

    fun errorHandler(ctx: RoutingContext, message: String): Handler<Throwable> {
      return Handler<Throwable> { error ->
        LOGGER.error("An Error Occurred: ${error}")
        ctx
          .response()
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(JsonObject().put("msg", message).toString())
      }
    }

    fun notFoundResponse(ctx: RoutingContext, message: String) {
      ctx
        .response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(
          JsonObject()
            .put("msg", message)
            .put("path", ctx.normalizedPath())
            .toBuffer()
        )
    }

  }
}
