package com.spotify.simplekotlinstandalone

import io.grpc.stub.StreamObserver
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import services.UserRequest
import services.UserResponse
import services.UserServiceGrpcKt.UserServiceImplBase
import kotlin.coroutines.experimental.EmptyCoroutineContext

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

  override fun getUser(request: UserRequest): Deferred<UserResponse> = GlobalScope.async {
    UserResponse
        .newBuilder()
        .setName(request.name ?: throw IllegalArgumentException("name can not be null"))
        .setEmailAddress("email")
        .setCountry("country")
        .setActive(true)
        .build()
  }
}
