package playground.adapter.api

import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod.GET
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus.OK
import com.microsoft.azure.functions.annotation.AuthorizationLevel.FUNCTION
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import org.springframework.stereotype.Component
import playground.adapter.iplookup.OutboundIpResolver

@Component
class IpLookupHandler(
    private val ipResolver: OutboundIpResolver
) {
    @Suppress("UnusedParameter")
    @FunctionName("getIpAddress")
    fun handleGetIpAddress(
        @HttpTrigger(
            name = "httpRequestTrigger",
            route = "ip-address",
            methods = [GET],
            authLevel = FUNCTION
        )
        request: HttpRequestMessage<String>,
        context: ExecutionContext
    ): HttpResponseMessage = request
        .createResponseBuilder(OK)
        .header("Content-Type", "application/json; charset=utf-8")
        .body("""{ "ip": "${ipResolver.resolve().value}" }""")
        .build()
}
