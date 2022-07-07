package armeria.example

import com.linecorp.armeria.common.HttpRequest
import com.linecorp.armeria.common.HttpResponse
import com.linecorp.armeria.common.util.UnmodifiableFuture
import com.linecorp.armeria.server.HttpService
import com.linecorp.armeria.server.ServiceRequestContext
import com.linecorp.armeria.server.SimpleDecoratingHttpService
import com.linecorp.armeria.server.auth.Authorizer
import io.netty.util.AttributeKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletionStage

class MyAuthenticator : Authorizer<HttpRequest> {
    override fun authorize(ctx: ServiceRequestContext, req: HttpRequest): CompletionStage<Boolean> {
        val credential = req.headers().get("credential")
        val isAuthenticated = (credential != null)
        if(isAuthenticated) {
            logger.debug("Authenticated user $credential")
        }
        return UnmodifiableFuture.completedFuture(isAuthenticated)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
