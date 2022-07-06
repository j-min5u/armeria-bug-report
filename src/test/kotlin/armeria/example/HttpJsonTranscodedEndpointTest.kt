package armeria.example

import com.linecorp.armeria.client.WebClient
import com.linecorp.armeria.client.grpc.GrpcClients
import com.linecorp.armeria.common.HttpStatus
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats
import com.linecorp.armeria.server.ServerBuilder
import com.linecorp.armeria.testing.junit5.server.ServerExtension
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import io.grpc.Metadata

class HttpJsonTranscodedEndpointTest {
    val webClient = WebClient.builder(server.httpUri()).build()
    @Test
    fun failIfNoCredentials() {
        runBlocking {
            val client = GrpcClients.newClient(uri(), HelloServiceGrpcKt.HelloServiceCoroutineStub::class.java)
            val exception: Throwable = assertThrows {
                client.helloAuth(helloRequest { name = "Dduckgu" })
            }
            assert(exception is StatusException)
            println((exception as StatusException).status.code)
            assert((exception as StatusException).status.code == Status.UNAUTHENTICATED.code)
        }
    }

    @Test
    fun succeedIfCredentialsExist() {
        runBlocking {
            val client = GrpcClients.newClient(uri(), HelloServiceGrpcKt.HelloServiceCoroutineStub::class.java)
            val metadata = Metadata().apply {
                put(
                    Metadata.Key.of(
                        "credential",
                        Metadata.ASCII_STRING_MARSHALLER
                    ),
                    1000.toString()
                )
            }
            val result = client
                .helloAuth(
                    request = helloRequest { name = "Dduckgu" },
                    headers = metadata
                ).message
            assert(result == "success")
        }
    }

    @Test
    fun httpJsonTranscodeURITest() {
        val response = webClient.get("/hello?name=dduckgu").aggregate().get()

        //fails
        assert(response.status() == HttpStatus.UNAUTHORIZED)
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            server.start()
        }

        @Suppress("unused")
        @JvmStatic
        @AfterAll
        fun afterAll() {
            server.stop()
        }

        val server = object : ServerExtension() {
            override fun configure(sb: ServerBuilder) {
                configureServices(sb)
            }
        }

        private fun uri(): String {
            return server.httpUri(GrpcSerializationFormats.PROTO).toString()
        }
    }
}