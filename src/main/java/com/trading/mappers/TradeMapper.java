package com.trading.mappers;

import com.broker.external.BrokerTradeSide;
import com.trading.dto.TradeRequest;
import com.trading.dto.TradeResponse;
import com.trading.dto.TradeStatusResponse;
import com.trading.models.Trade;

import java.time.LocalDateTime;

import static com.trading.models.TradeStatus.PENDING_EXECUTION;

public class TradeMapper {
    public static Trade mapTradeRequestToTrade(TradeRequest tradeRequest, BrokerTradeSide side) {
        Trade trade = new Trade();
        trade.setSymbol(tradeRequest.getSymbol());
        trade.setQuantity(tradeRequest.getQuantity());
        trade.setSide(side);
        trade.setPrice(tradeRequest.getPrice());
        trade.setStatus(PENDING_EXECUTION);
        trade.setTimestamp(LocalDateTime.now());

        return trade;
    }

    public static TradeResponse mapTradeToTradeResponse(Trade trade) {
        TradeResponse response = new TradeResponse();
        response.setId(trade.getId());
        response.setSymbol(trade.getSymbol());
        response.setQuantity(trade.getQuantity());
        response.setSide(trade.getSide());
        response.setPrice(trade.getPrice());
        response.setStatus(trade.getStatus());
        response.setReason(trade.getReason());
        response.setTimestamp(trade.getTimestamp().toString());
        return response;
    }

    public static TradeStatusResponse mapTradeToTradeStatusResponse(Trade trade) {
        return new TradeStatusResponse(trade.getStatus());
    }
}
