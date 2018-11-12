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

import io.grpc.stub.StreamObserver
import services.UserRequest
import services.UserResponse
import services.UserServiceGrpcKt.UserServiceImplBase

class UserService : UserServiceImplBase() {

  /* override */ fun getUserOld(request: UserRequest, responseObserver: StreamObserver<UserResponse>) {
    val response = UserResponse
        .newBuilder()
        .setName(request.name ?: throw IllegalArgumentException("name can not be null"))
        .setEmailAddress("email")
        .setCountry("country")
        .setActive(true)
        .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }

  override suspend fun getUser(request: UserRequest): UserResponse {
    return UserResponse
        .newBuilder()
        .setName(request.name ?: throw IllegalArgumentException("name can not be null"))
        .setEmailAddress("email")
        .setCountry("country")
        .setActive(true)
        .build()
  }
}
