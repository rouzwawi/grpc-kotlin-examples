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

  Runtime.getRuntime().addShutdownHook(Thread {
    println("Ups, JVM shutdown")

    chatService.shutdown()
    server.shutdown()
    server.awaitTermination()

    println("User service stopped")
  })

  server.start()
  println("User service started")
  server.awaitTermination()
}
