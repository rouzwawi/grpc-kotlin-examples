package com.spotify.simplekotlinstandalone

fun main(args: Array<String>) {
    when(args[0]) {
        "server" -> GrpcServer.grpcServer()
        "client" -> ChatClient.chatClient()
    }
}
