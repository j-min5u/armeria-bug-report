package armeria.example

import com.linecorp.armeria.server.ServerBuilder
import com.linecorp.armeria.server.grpc.GrpcService
import io.grpc.protobuf.services.ProtoReflectionService

fun configureServices(sb: ServerBuilder) {
    val grpcService = GrpcService.builder()
        // https://github.com/grpc/grpc/blob/master/doc/health-checking.md
        .enableHealthCheckService(true)
        // DocService 에서 json body 로 grpc requst 가 가능해집니다.
        .enableUnframedRequests(true)
        // https://cloud.google.com/endpoints/docs/grpc/transcoding
        .enableHttpJsonTranscoding(true)
        // Block 발생시 별도의 executor 를 이용해 처리하도록 하는 옵션
        .useBlockingTaskExecutor(true)
        .addService(ProtoReflectionService.newInstance())
        .addService(HelloServiceImpl())
        .build()

    sb.service(grpcService)
}
