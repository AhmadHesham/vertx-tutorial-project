package com.learning.vertx_web_tutorial.quotes

import com.learning.vertx_web_tutorial.assets.Asset
import com.learning.vertx_web_tutorial.assets.AssetRestApi
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.util.concurrent.ThreadLocalRandom

class QuotesRestApi {

  companion object {
    private val LOGGER = LoggerFactory.getLogger(QuotesRestApi::class.java)
    private val cachedQuotes: HashMap<String, Quote> = HashMap()

    fun attach(router: Router, db: Pool) {
      AssetRestApi.ASSETS.forEach { asset ->
        cachedQuotes[asset] = Quote(Asset(asset), randomValue(), randomValue(), randomValue(), randomValue())
      }
      router.apply {
        get("/quotes/:asset").handler(handleQuoteAsset)
        get("/quotes/pg/:asset").handler(GetQuotesFromDatabaseHandler(db))
      }
    }

    private val handleQuoteAsset = Handler<RoutingContext> { rc ->
      val asset = rc.pathParam("asset")
      LOGGER.debug("Asset Parameter: $asset")

      val maybeQuote = cachedQuotes[asset]
      if(maybeQuote == null) {
        rc.response().statusCode = HttpResponseStatus.NOT_FOUND.code()
        rc.response().end(JsonObject().put("msg", "quote for asset $asset not available").toString())
        return@Handler
      }

      val jsonObject: JsonObject = maybeQuote.toJson()
      LOGGER.debug(jsonObject.toString())
      rc.response().end(jsonObject.toString())

      LOGGER.info("PATH ${rc.normalizedPath()}")
    }

    private fun randomValue(): BigDecimal {
      return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1.0, 100.0))
    }
  }
}
