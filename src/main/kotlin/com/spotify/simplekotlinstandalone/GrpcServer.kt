package com.spotify.simplekotlinstandalone

import io.grpc.ServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService

fun main(args: Array<String>) {

//    val channel = ManagedChannelBuilder.forAddress("localhost", 15000).usePlaintext(true).build()

  val server = ServerBuilder
      .forPort(15001)
      .addService(ProtoReflectionService.newInstance())
      .addService(UserService()).build()
  server.start()

  println("User service started")

  Runtime.getRuntime().addShutdownHook(Thread { println("Ups, JVM shutdown") })
  server.awaitTermination()

  println("User service stopped")
}
