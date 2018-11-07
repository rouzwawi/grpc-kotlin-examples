package com.spotify.simplekotlinstandalone

import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import services.ChatMessage
import services.ChatMessageFromService
import services.ChatServiceGrpcKt

fun main(args: Array<String>) {
    ChatClient.chatClient()
}

object ChatClient {

    fun chatClient() {
        val channel = ManagedChannelBuilder.forAddress("localhost", 15001)
            .usePlaintext()
            .build()

        val chatService = ChatServiceGrpcKt.newStub(channel)

        val chat = chatService.chat()

        println("type :q to quit")
        print("From: ")
        val from = readLine()
        chat.send(
            ChatMessage.newBuilder()
                .setFrom(from)
                .build()
        )

        startPrintLoop(chat)

        try {
            while (true) {
                print("Message: ")
                val message = readLine()
                if (message == null || message == ":q") {
                    break
                }
                chat.send(
                    ChatMessage.newBuilder()
                        .setFrom(from)
                        .setMessage(message)
                        .build()
                )
            }
        } finally {
            println("closing")
            chat.close()
        }
    }

    private fun startPrintLoop(chat: ReceiveChannel<ChatMessageFromService>) = GlobalScope.launch {
        try {
            for (responseMessage in chat) {
                val message = responseMessage.message
                println("${message.from}: ${message.message}")
            }
            println("Server disconnected")
        } catch (e: Throwable) {
            println("Server disconnected badly: $e")
        }
    }
}
