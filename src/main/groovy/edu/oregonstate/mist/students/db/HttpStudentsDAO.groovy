package edu.oregonstate.mist.students.db

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import edu.oregonstate.mist.students.core.AccountBalance
import groovy.transform.InheritConstructors
import org.apache.http.HttpHeaders
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.core.UriBuilder

class HttpStudentsDAO {
    private HttpClient httpClient
    private final URI baseURI

    private final String accountBalanceEndpoint = "account-balances"

    private static Logger logger = LoggerFactory.getLogger(this)

    ObjectMapper objectMapper = new ObjectMapper()

    HttpStudentsDAO(HttpClient httpClient, String endpoint) {
        this.httpClient = httpClient
        this.baseURI = UriBuilder.fromUri(endpoint).path("/api").build()
    }

    protected AccountBalance getAccountBalance(String id) {
        HttpResponse response = getResponse("$accountBalanceEndpoint/$id")

        String responseEntity = EntityUtils.toString(response.entity)

        BackendAccountBalance accountBalance = objectMapper.readValue(
                responseEntity, BackendAccountBalance)

        AccountBalance.fromBackendAccountBalance(accountBalance)
    }

    public String healthCheck() {
        HttpResponse httpResponse = getResponse("healthcheck")

        String response = EntityUtils.toString(httpResponse.entity)

        String status = httpResponse.statusLine.toString()

        logger.info("Response status: $status")

        logger.info(response)

        response
    }

    private HttpResponse getResponse(String endpoint) {
        UriBuilder uriBuilder = UriBuilder.fromUri(baseURI)
        uriBuilder.path(endpoint)

        URI requestURI = uriBuilder.build()

        HttpGet request = new HttpGet(requestURI)
        request.setHeader(HttpHeaders.ACCEPT, "application/json")

        logger.info("Making a request to ${requestURI}")

        httpClient.execute(request)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BackendAccountBalance {
    BigDecimal balance
}
