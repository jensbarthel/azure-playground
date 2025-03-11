package playground.adapter.iplookup

import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse

@Component
class OutboundIpResolver {

    fun resolve(): IpAddress {
        val client = HttpClient.newHttpClient()
        val request = HttpRequest
            .newBuilder()
            .uri(URI("http://ipecho.net/plain"))
            .method("GET", BodyPublishers.noBody())
            .build()

        val httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString())
        require(httpResponse.statusCode() < HTTP_ERROR_CODE_RANGE)

        return IpAddress(httpResponse.body())
    }

    companion object {
        private const val HTTP_ERROR_CODE_RANGE = 400
    }
}

data class IpAddress(val value: String)
