package com.spotify.simplekotlinstandalone

import com.spotify.grpc.ServerLimits
import com.spotify.grpc.metric.semantic.SemanticMetricServerInterceptor
import com.spotify.metrics.core.SemanticMetricRegistry
import io.grpc.ServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService

fun main(args: Array<String>) {
    GrpcServer.grpcServer()
}

object GrpcServer {

    fun grpcServer() {
        val registry = SemanticMetricRegistry()
        val chatService = ChatService()
        val server = ServerBuilder
            .forPort(15001)
            .intercept(SemanticMetricServerInterceptor(registry))
            .intercept(ServerLimits.simpleServerLimiter(registry))
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
}
