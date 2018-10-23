package com.spotify.simplekotlinstandalone

import io.grpc.stub.StreamObserver
import services.UserRequest
import services.UserResponse
import services.UserServiceGrpc.UserServiceImplBase

//class UserService(keyValue: KeyValueServiceBlockingStub) : UserServiceImplBase() {

class UserService : UserServiceImplBase() {
  //    private val keyValue = Preconditions.checkNotNull(keyValue)
//
  override fun getUser(request: UserRequest, responseObserver: StreamObserver<UserResponse>) {
    val response = UserResponse
        .newBuilder()
        .setName(request?.name ?: throw IllegalArgumentException("name can not be null"))
        .setEmailAddress("email")
        .setCountry("country")
        .setActive(true)
        .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }
}
