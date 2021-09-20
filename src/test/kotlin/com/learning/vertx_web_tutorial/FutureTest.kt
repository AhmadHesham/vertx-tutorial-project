package com.learning.vertx_web_tutorial

import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.RuntimeException
import javax.management.RuntimeErrorException

@ExtendWith(VertxExtension::class)
class FutureTest {

  @Test
  fun test_promise(vertx: Vertx, testContext: VertxTestContext) {
    val promise: Promise<String> = Promise.promise()
    promise.complete("Hello There")
    val future = promise.future()
    future.onSuccess { result ->
      println("Result: $result")
    }
      .compose { something ->
        println("something: $something")
        val anotherPromise: Promise<String> = Promise.promise()
        anotherPromise.complete("Another Hello")
        val future = promise.future()
        future.onSuccess { hehe ->
          println("Hehe: $hehe")
          testContext.completeNow()
        }
      }
  }
}
