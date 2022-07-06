package armeria.example

import com.linecorp.armeria.server.HttpService
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
        return AuthService.newDecorator(
            MyAuthenticator()
        )
    }
}
