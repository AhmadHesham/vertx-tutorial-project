package com.learning.vertx_web_tutorial.watchlist

import com.learning.vertx_web_tutorial.db.DBResponse
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.templates.SqlTemplate
import org.slf4j.LoggerFactory
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap

class PutWatchlistDatabaseHandler(private val db: Pool) : Handler<RoutingContext> {
  private val LOGGER = LoggerFactory.getLogger(PutWatchlistDatabaseHandler::class.java)

  override fun handle(ctx: RoutingContext) {
    val accountId: String = ctx.pathParam("accountId")
    val body = ctx.bodyAsJson
    val watchlist: WatchList = body.mapTo(WatchList::class.java)

    val parameterBatch: List<HashMap<String, Any>> = watchlist
      .assets
      .stream()
      .map { asset ->
        val hashmap: HashMap<String, Any> = HashMap()
        hashmap["account_id"] = accountId
        hashmap["asset"] = asset.name
        return@map hashmap
      }
      .collect(Collectors.toList())

    db.withTransaction { client ->
      SqlTemplate
        .forUpdate(client, "DELETE FROM broker.watchlist w where w.account_id = #{account_id}")
        .execute(Collections.singletonMap("account_id", accountId) as Map<String, Any>?)
        .onFailure(DBResponse.errorHandler(ctx, "Failed to clear watchlist for accountId: $accountId"))
        .compose { deletionDone ->
          // .forUpdate should be used for insert, update or delete from the DB
          SqlTemplate
            .forUpdate(
              client,
              "INSERT INTO broker.watchlist VALUES (#{account_id}, #{asset}) ON CONFLICT (account_id, asset) DO NOTHING"
            )
            .executeBatch(parameterBatch)
            .onFailure(DBResponse.errorHandler(ctx, "Failed to insert into watchlist"))

        }
        .onFailure(DBResponse.errorHandler(ctx, "Failed to update watchlist for accountId: $accountId"))
        .onSuccess { result ->
          ctx
            .response()
            .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
            .end()
        }
    }
  }

}
