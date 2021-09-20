package com.learning.vertx_web_tutorial.quotes

import com.fasterxml.jackson.annotation.JsonProperty
import com.learning.vertx_web_tutorial.assets.Asset
import io.vertx.core.json.JsonObject
import lombok.Builder
import lombok.Value
import java.math.BigDecimal

@Value
class Quote(
  val asset: Asset?,
  val bid: BigDecimal,
  val ask: BigDecimal,
  @JsonProperty("last_price")
  val lastPrice: BigDecimal,
  val volume: BigDecimal,
) {

  fun toJson(): JsonObject {
    return JsonObject.mapFrom(this)
  }

  constructor(): this(null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)


}
