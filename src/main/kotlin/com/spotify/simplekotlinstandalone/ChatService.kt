package com.spotify.simplekotlinstandalone

import com.google.protobuf.Timestamp
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.produce
import services.ChatMessage
import services.ChatMessageFromService
import services.ChatServiceGrpcKt
import kotlin.collections.LinkedHashSet


class ChatService : ChatServiceGrpcKt.ChatServiceImplBase() {
    private val clientChannels = LinkedHashSet<SendChannel<ChatMessageFromService>>()

    override fun chat(requestChannel: ReceiveChannel<ChatMessage>): ReceiveChannel<ChatMessageFromService> = GlobalScope.produce {
        println("New connection: $channel")
        clientChannels.add(channel)
        channel.invokeOnClose {
            it?.printStackTrace()
        }

        for (request in requestChannel) {
            println("Got request from $requestChannel: $request")
            val channelsCopy = LinkedHashSet(clientChannels)
            for (client in channelsCopy) {
                if (client.isClosedForSend) {
                    println("Removing channel $client")
                    clientChannels.remove(client)
                } else {
                    println("Sending to $client")
                    client.send(ChatMessageFromService.newBuilder()
                            .setTimestamp(Timestamp.newBuilder()
                                    .setSeconds(System.nanoTime() / 1000000000)
                                    .setNanos((System.nanoTime() % 1000000000).toInt())
                                    .build())
                            .setMessage(request)
                            .build())
                }
            }
        }
    }

    fun kill() {
        println("Killed")
        clientChannels.stream().forEach{client ->
            println("Closing channel $client")
            client.close()
        }
        clientChannels.clear()
    }
}
