package com.learning.vertx_web_tutorial.watchlist

import com.learning.vertx_web_tutorial.assets.Asset
import io.vertx.core.json.JsonObject
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import lombok.Value

class WatchList(val assets: MutableList<Asset>) {

  constructor(): this(mutableListOf())

  fun toJsonObject(): JsonObject{
    return JsonObject.mapFrom(this)
  }
}
