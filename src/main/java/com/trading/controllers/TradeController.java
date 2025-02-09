package com.trading.controllers;

import com.broker.external.BrokerTradeSide;
import com.trading.dto.TradeRequest;
import com.trading.dto.TradeResponse;
import com.trading.dto.TradeStatusResponse;
import com.trading.mappers.TradeMapper;
import com.trading.models.Trade;
import com.trading.services.TradeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/buy")
    public ResponseEntity<Void> submitBuyTrade(@RequestBody @Valid TradeRequest tradeRequest) {
        return processTrade(tradeRequest, BrokerTradeSide.BUY);
    }

    @PostMapping("/sell")
    public ResponseEntity<Void> submitSellTrade(@RequestBody @Valid TradeRequest tradeRequest) {
        return processTrade(tradeRequest, BrokerTradeSide.SELL);
    }

    @GetMapping("/trades/{tradeId}/status")
    public ResponseEntity<TradeStatusResponse> getTradeStatus(@PathVariable UUID tradeId) {
        Trade trade = tradeService.getTradeDetails(tradeId);
        return ResponseEntity.ok(TradeMapper.mapTradeToTradeStatusResponse(trade));
    }

    @GetMapping("/trades/{tradeId}")
    public ResponseEntity<TradeResponse> getTradeDetails(@PathVariable UUID tradeId) {
        Trade trade = tradeService.getTradeDetails(tradeId);
        return ResponseEntity.ok(TradeMapper.mapTradeToTradeResponse(trade));
    }

    @GetMapping("/trades")
    public ResponseEntity<List<TradeResponse>> getAllTrades() {
        List<Trade> tradesList = tradeService.getAllTrades();
        return ResponseEntity.ok(tradesList.stream().map(TradeMapper::mapTradeToTradeResponse).toList());
    }

    private ResponseEntity<Void> processTrade(TradeRequest tradeRequest, BrokerTradeSide tradeSide) {
        Trade trade = tradeService.submitTrade(TradeMapper.mapTradeRequestToTrade(tradeRequest, tradeSide));
        URI location = buildLocationUri(trade.getId());
        return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, location.toString()).build();
    }

    private URI buildLocationUri(UUID tradeId) {
        return linkTo(methodOn(TradeController.class).getTradeStatus(tradeId)).toUri();
    }
}
