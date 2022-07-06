package armeria.example


class HelloServiceImpl : HelloServiceGrpcKt.HelloServiceCoroutineImplBase() {
    @Authenticate
    override suspend fun helloAuth(request: Hello.HelloRequest): Hello.HelloReply {
        return helloReply {
            message = "success"
        }
    }
}
