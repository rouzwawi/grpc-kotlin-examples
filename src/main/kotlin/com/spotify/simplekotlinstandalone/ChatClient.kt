package com.spotify.simplekotlinstandalone

import com.spotify.grpc.LoggingClientInterceptor
import com.spotify.grpc.metric.semantic.SemanticMetricClientInterceptor
import com.spotify.metrics.core.SemanticMetricRegistry
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import services.ChatMessage
import services.ChatServiceGrpcKt

fun main(args: Array<String>) {
  chatClient()
}

fun chatClient() = runBlocking {
  val registry = SemanticMetricRegistry()

  val channel = ManagedChannelBuilder
      .forAddress("localhost", 15001)
      .enableFullStreamDecompression()
//      .nameResolverFactory(NamelessNameResolverProvider())
      .intercept(SemanticMetricClientInterceptor(registry))
      .intercept(LoggingClientInterceptor())
      .usePlaintext()
      .build()

  val chatService = ChatServiceGrpcKt
      .newStub(channel)
      .withCompression("gzip")
  val chat = chatService.chat()

  launch(Dispatchers.Default) {
    try {
      for (responseMessage in chat) {
        println(responseMessage)
      }
      println("Server disconnected")
    } catch (e: Throwable) {
      println("Server disconnected badly: $e")
    }
  }

  try {
    while (true) {
      print("From: ")
      val from = readLine()
      print("Message: ")
      val message = readLine()
      chat.send(ChatMessage.newBuilder()
          .setFrom(from)
          .setMessage(message)
          .build())
    }
  } finally {
    chat.close()
  }
}
