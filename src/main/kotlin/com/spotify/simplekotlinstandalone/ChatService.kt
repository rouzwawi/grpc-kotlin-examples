package com.spotify.simplekotlinstandalone

import com.google.protobuf.Timestamp
import io.grpc.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.produce
import services.ChatMessage
import services.ChatMessageFromService
import services.ChatServiceGrpcKt

@UseExperimental(ExperimentalCoroutinesApi::class)
class ChatService : ChatServiceGrpcKt.ChatServiceImplBase() {

    data class Client(val name: String, val channel: SendChannel<ChatMessageFromService>)

    private val clientChannels = LinkedHashSet<Client>()

    override suspend fun chat(requests: ReceiveChannel<services.ChatMessage>) = produce<ChatMessageFromService> {
        println("New client connection: $channel")

        // wait for first message
        val hello = requests.receive()
        val name = hello.from
        val client = Client(name, this)
        clientChannels.add(client)
        channel.invokeOnClose {
            it?.printStackTrace()
        }

        try {
            for (chatMessage in requests) {
                println("Got request from $requests:")
                println(chatMessage)
                val message = createMessage(chatMessage)
                clientChannels
                    .filter { it.name != chatMessage.from }
                    .forEach { other ->
                        println("Sending to $other")
                        other.channel.send(message)
                    }
            }
        } catch (t: Throwable) {
            println("Threw $t")
            if (Status.fromThrowable(t).code != Status.Code.CANCELLED) {
                println("An actual error occurred")
                t.printStackTrace()
            }
        } finally {
            println("Connection with client closed. Removing request and client channels from ChatService")
            clientChannels.remove(client)
        }
    }

    fun shutdown() {
        println("Shutting down Chat service")
        clientChannels.stream().forEach { client ->
            println("Closing client channel $client")
            client.channel.close()
        }
        clientChannels.clear()
    }

    private fun createMessage(request: ChatMessage): ChatMessageFromService {
        return ChatMessageFromService.newBuilder()
            .setTimestamp(
                Timestamp.newBuilder()
                    .setSeconds(System.nanoTime() / 1000000000)
                    .setNanos((System.nanoTime() % 1000000000).toInt())
                    .build()
            )
            .setMessage(request)
            .build()
    }
}
