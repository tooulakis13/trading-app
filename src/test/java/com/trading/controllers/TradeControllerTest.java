package com.trading.controllers;

import com.trading.dto.TradeRequest;
import com.trading.dto.TradeResponse;
import com.trading.dto.TradeStatusResponse;
import com.trading.exceptions.InternalServerErrorException;
import com.trading.models.Trade;
import com.trading.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TradeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TradeRepository tradeRepository;

    private TradeRequest tradeRequest;
    private TradeRequest tradeRequestInvalid;

    @BeforeEach
    void setUp() {
        tradeRequest = new TradeRequest("USD/JPY", 100, new BigDecimal("110.50"));
        tradeRequestInvalid = new TradeRequest(null, 100, new BigDecimal("110.50"));
    }

    @Test
    void submitBuyTrade_ShouldReturnCreated() {
        ResponseEntity<Void> response = createTrade("/api/v1/buy", tradeRequest, Void.class);

        tradeAssertions(response);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
    }

    @Test
    void submitBuyTrade_ShouldReturnBadRequest() {
        ResponseEntity<String> response = createTrade("/api/v1/buy", tradeRequestInvalid, String.class);
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("symbol - must not be null");
    }

    @Test
    void submitSellTrade_ShouldReturnCreated() {
        ResponseEntity<Void> response = createTrade("/api/v1/sell", tradeRequest, Void.class);

        tradeAssertions(response);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
    }

    @Test
    void submitSellTrade_ShouldReturnBadRequest() {
        ResponseEntity<String> response = createTrade("/api/v1/sell", tradeRequestInvalid, String.class);
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("symbol - must not be null");
    }

    @Test
    void getTradeStatus_ShouldReturnOk() {
        UUID tradeId = getTradeId(createTrade("/api/v1/buy", tradeRequest, Void.class));
        ResponseEntity<TradeStatusResponse> response = restTemplate.getForEntity("/api/v1/trades/" + tradeId + "/status", TradeStatusResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getTradeDetails_ShouldReturnOk() {
        UUID tradeId = getTradeId(createTrade("/api/v1/buy", tradeRequest, Void.class));
        ResponseEntity<TradeResponse> response = restTemplate.getForEntity("/api/v1/trades/" + tradeId, TradeResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getTradeStatus_ShouldReturnNotFound() {
        UUID tradeId = UUID.randomUUID();
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/trades/" + tradeId + "/status", String.class);
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void getTradeDetails_ShouldReturnNotFound() {
        UUID tradeId = UUID.randomUUID();
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/trades/" + tradeId, String.class);
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void getAllTrades_ShouldReturnOk() {
        ResponseEntity<List<TradeResponse>> response = restTemplate.exchange("/api/v1/trades", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    private <T> ResponseEntity<T> createTrade(String url, TradeRequest tradeRequest, Class<T> responseType) {
        return restTemplate.postForEntity(url, tradeRequest, responseType);
    }

    private UUID getTradeId(ResponseEntity<Void> response) {
        URI locationUri = response.getHeaders().getLocation();

        if (locationUri == null) {
            throw new InternalServerErrorException("Location header is missing in the response");
        }

        String location = locationUri.toString();
        return UUID.fromString(location.replaceAll(".*/([a-f0-9\\-]{36})/.*", "$1"));
    }

    private void tradeAssertions(ResponseEntity<Void> response) {
        UUID tradeId = getTradeId(response);

        Optional<Trade> tradeOptional = tradeRepository.findById(tradeId);

        assertThat(tradeOptional.isPresent()).isTrue();
        assertThat(tradeOptional.get().getSymbol()).isEqualTo(tradeRequest.getSymbol());
        assertThat(tradeOptional.get().getQuantity()).isEqualTo(tradeRequest.getQuantity());
        assertThat(tradeOptional.get().getPrice()).isEqualTo(tradeRequest.getPrice());
    }
}