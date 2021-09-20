package com.learning.vertx_web_tutorial.watchlist

import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.HashMap

class WatchListRestApi {

  companion object {
    private val LOGGER = LoggerFactory.getLogger(WatchListRestApi::class.java)
    private val PATH = "/account/watchlist/:accountId"
    private val PG_PATH = "/pg/account/watchlist/:accountId"
    private val watchListPerAccount: HashMap<UUID, WatchList> = HashMap()

    fun attach(router: Router, db: Pool) {
      router.apply {
        // GET METHOD
        get(PATH).handler(accountsWatchList)

        // PUT METHOD
        put(PATH).handler(createWatchList)

        // DELETE METHOD
        delete(PATH).handler(deleteWatchList)

        //Database METHOD
        get(PG_PATH).handler(GetWatchlistFromDatabaseHandler(db))
        put(PG_PATH).handler(PutWatchlistDatabaseHandler(db))
        delete(PG_PATH).handler(DeleteWatchlistDatabaseHandler(db))
      }
    }

    private val accountsWatchList = Handler<RoutingContext> { rc ->
      val accountId = rc.pathParam("accountId")
      LOGGER.debug("${rc.normalizedPath()} for account $accountId")
      val watchList = watchListPerAccount[UUID.fromString(accountId)]
      rc.response().end(watchList?.toJsonObject()?.toString()?: JsonObject().put("msg", "No watchlist available for this account").toString())
    }

    private val createWatchList = Handler<RoutingContext> { rc ->
      val accountId = rc.pathParam("accountId")
      val watchList: WatchList = rc.bodyAsJson.mapTo(WatchList::class.java)
      watchListPerAccount[UUID.fromString(accountId)] = watchList
      rc.response().end(watchList.toJsonObject().toString())
    }

    private val deleteWatchList = Handler<RoutingContext> { rc ->
      val accountId = rc.pathParam("accountId")
      val watchlist: WatchList? = watchListPerAccount.remove(UUID.fromString(accountId))

      val response: JsonObject = watchlist?.toJsonObject() ?: JsonObject().put("msg", "Invalid WatchList")
      rc.response().end(response.toBuffer())
    }

  }
}
