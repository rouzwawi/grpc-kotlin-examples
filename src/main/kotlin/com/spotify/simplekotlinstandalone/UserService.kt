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
