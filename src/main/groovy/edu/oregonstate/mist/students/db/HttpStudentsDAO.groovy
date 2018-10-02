package edu.oregonstate.mist.students.db

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import edu.oregonstate.mist.students.core.AccountBalance
import edu.oregonstate.mist.students.core.AccountTransactions
import org.apache.http.HttpHeaders
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.core.UriBuilder
import java.time.Instant
import java.time.ZonedDateTime

class HttpStudentsDAO {
    private HttpClient httpClient
    private final URI baseURI

    private final String studentsEndpoint = "students"
    private final String accountBalanceEndpoint = "account-balances"
    private final String accountTransactionsEndpoint = "account-transactions"

    private static Logger logger = LoggerFactory.getLogger(this)

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())

    HttpStudentsDAO(HttpClient httpClient, String endpoint) {
        this.httpClient = httpClient
        this.baseURI = UriBuilder.fromUri(endpoint).path("/api").build()
    }

    protected AccountBalance getAccountBalance(String id) {
        String response = getResponse("$accountBalanceEndpoint/$id")

        BackendAccountBalance accountBalance = objectMapper.readValue(
                response, BackendAccountBalance)

        AccountBalance.fromBackendAccountBalance(accountBalance)
    }

    protected AccountTransactions getAccountTransactions(String id) {
        String response = getResponse("$studentsEndpoint/$id/$accountTransactionsEndpoint")

        List<BackendAccountTransaction> accountTransactions = objectMapper.readValue(
                response, new TypeReference<List<BackendAccountTransaction>>() {})

        AccountTransactions.fromBackendAccountTransactions(accountTransactions)
    }

    public String healthCheck() {
        HttpResponse httpResponse = getResponse("healthcheck")

        String response = EntityUtils.toString(httpResponse.entity)

        String status = httpResponse.statusLine.toString()

        logger.info("Response status: $status")

        logger.info(response)

        response
    }

    private String getResponse(String endpoint) {
        UriBuilder uriBuilder = UriBuilder.fromUri(baseURI)
        uriBuilder.path(endpoint)

        URI requestURI = uriBuilder.build()

        HttpGet request = new HttpGet(requestURI)
        request.setHeader(HttpHeaders.ACCEPT, "application/json")

        logger.info("Making a request to ${requestURI}")

        HttpResponse response = httpClient.execute(request)

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            EntityUtils.consumeQuietly(response.entity)
            throw new StudentNotFoundException("Student not found.")
        } else {
            EntityUtils.toString(response.entity)
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BackendAccountBalance {
    BigDecimal balance
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BackendAccountTransaction {
    BigDecimal amount
    String description
    Instant entryDate
}
