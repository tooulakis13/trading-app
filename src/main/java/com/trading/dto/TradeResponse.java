package com.trading.dto;

import com.broker.external.BrokerTradeSide;
import com.trading.models.TradeStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TradeResponse {
    private UUID id;
    private String symbol;
    private long quantity;
    private BrokerTradeSide side;
    private BigDecimal price;
    private TradeStatus status;
    private String reason;
    private String timestamp;
}
