package com.spotify.simplekotlinstandalone

import io.grpc.ServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService

fun main(args: Array<String>) {
  val chatService = ChatService()
  val server = ServerBuilder
      .forPort(15001)
      .addService(ProtoReflectionService.newInstance())
      .addService(UserService())
      .addService(chatService)
      .build()
  server.start()

  println("User service started")

  Runtime.getRuntime().addShutdownHook(Thread { println("Ups, JVM shutdown") })

  try {
    // Only so you can kill the service easily...
    readLine()
  } finally {
    chatService.kill()
    server.shutdown()
    server.awaitTermination()

    println("User service stopped")
  }
}
