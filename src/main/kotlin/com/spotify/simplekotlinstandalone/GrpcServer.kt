/*-
 * -\-\-
 * simple-kotlin-standalone-example
 * --
 * Copyright (C) 2016 - 2018 rouz.io
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

package com.spotify.simplekotlinstandalone

import io.grpc.ServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService

fun main(args: Array<String>) {
    GrpcServer.grpcServer()
}

object GrpcServer {

    fun grpcServer() {
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
}
