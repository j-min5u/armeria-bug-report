package armeria.example

import com.linecorp.armeria.common.HttpRequest
import com.linecorp.armeria.common.HttpResponse
import com.linecorp.armeria.common.ResponseHeaders
import com.linecorp.armeria.common.ResponseHeadersBuilder
import com.linecorp.armeria.server.HttpService
import com.linecorp.armeria.server.ServiceRequestContext
import com.linecorp.armeria.server.SimpleDecoratingHttpService
import com.linecorp.armeria.server.annotation.DecoratorFactory
import com.linecorp.armeria.server.annotation.DecoratorFactoryFunction
import com.linecorp.armeria.server.auth.AuthService
import java.util.function.Function

@DecoratorFactory(MyAuthDecoratorFactoryFunction::class)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
annotation class Authenticate

class MyAuthDecoratorFactoryFunction : DecoratorFactoryFunction<Authenticate> {
    override fun newDecorator(parameter: Authenticate): Function<in HttpService, out HttpService> {
        return Function{ delegate: HttpService ->
            TemporaryGrpcStatusHeaderInserter(
                AuthService.newDecorator(
                    MyAuthenticator()
                ).apply(delegate)
            )
        }
    }
}

class TemporaryGrpcStatusHeaderInserter(
    private val delegate: HttpService
) : SimpleDecoratingHttpService(delegate) {
    override fun serve(ctx: ServiceRequestContext, req: HttpRequest): HttpResponse {
        return delegate.serve(ctx, req).mapHeaders { headers ->
            if(headers["grpc-status"] == null) {
                return@mapHeaders ResponseHeaders.builder()
                    .add(headers)
                    .add("grpc-status", "16")
                    .build()
            }
            headers
        }
    }
}
