package com.learning.vertx_web_tutorial.quotes

import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.json.JsonObject
import lombok.Data
import java.math.BigDecimal

@Data
class QuoteEntity(
  @JsonProperty("asset")
  val asset: String,
  @JsonProperty("bid")
  val bid: BigDecimal,
  @JsonProperty("ask")
  val ask: BigDecimal,
  @JsonProperty("last_price")
  val lastPrice: BigDecimal,
  @JsonProperty("volume")
  val volume: BigDecimal,
){

  constructor(): this("", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
    BigDecimal.ZERO)

  fun toJson(): JsonObject {
    return JsonObject.mapFrom(this)
  }

}
