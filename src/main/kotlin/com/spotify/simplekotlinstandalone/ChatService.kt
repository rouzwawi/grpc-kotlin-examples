package com.spotify.simplekotlinstandalone

import com.google.protobuf.Timestamp
import io.grpc.Status
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import services.ChatMessage
import services.ChatMessageFromService
import services.ChatServiceGrpcKt


class ChatService : ChatServiceGrpcKt.ChatServiceImplBase() {
  private val clientChannels = LinkedHashSet<SendChannel<ChatMessageFromService>>()

  override fun chat(requestChannel: ReceiveChannel<ChatMessage>):
      ReceiveChannel<ChatMessageFromService> = GlobalScope.produce {
    println("New client connection: $channel")
    clientChannels.add(channel)
    channel.invokeOnClose {
      it?.printStackTrace()
    }

    try {
      requestChannel.consumeEach { chatMessage ->
        println("Got request from $requestChannel: $chatMessage")
        val message = createMessage(chatMessage)
        clientChannels.forEach { clientChannel ->
          println("Sending to $clientChannel")
          clientChannel.send(message)
        }
      }
    } catch (t: Throwable) {
      if (Status.fromThrowable(t).code != Status.Code.CANCELLED) {
        println("An actual error occurred")
        t.printStackTrace()
      }
    } finally {
      println("Connection with client closed. Removing request and client channels from ChatService")
      requestChannel.cancel()
      clientChannels.remove(channel)
    }
  }

  fun shutdown() {
    println("Shutting down Chat service")
    clientChannels.stream().forEach { client ->
      println("Closing client channel $client")
      client.close()
    }
    clientChannels.clear()
  }

  private fun createMessage(request: ChatMessage): ChatMessageFromService {
    return ChatMessageFromService.newBuilder()
        .setTimestamp(Timestamp.newBuilder()
            .setSeconds(System.nanoTime() / 1000000000)
            .setNanos((System.nanoTime() % 1000000000).toInt())
            .build())
        .setMessage(request)
        .build()
  }
}
