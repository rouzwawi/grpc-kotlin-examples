package com.spotify.simplekotlinstandalone

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
  val channel = ManagedChannelBuilder
      .forAddress("localhost", 15001)
      .usePlaintext()
      .build()

  val chatService = ChatServiceGrpcKt.newStub(channel)
  val chat = chatService.chat()

  launch(Dispatchers.Default) {
    try {
      for (responseMessage in chat.response) {
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
      chat.request.send(ChatMessage.newBuilder()
          .setFrom(from)
          .setMessage(message)
          .build())
    }
  } finally {
    chat.request.close()
  }
}
