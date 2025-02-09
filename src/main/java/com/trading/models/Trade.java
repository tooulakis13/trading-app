package com.trading.models;

import com.broker.external.BrokerTradeSide;
import com.trading.dto.TradeStatusResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.trading.models.TradeStatus.PENDING_EXECUTION;

@Data
@Entity
public class Trade {
    @Id
    @GeneratedValue
    private UUID id;
    private String symbol;
    private long quantity;
    private BrokerTradeSide side;
    private BigDecimal price;
    private TradeStatus status;
    private String reason;
    private LocalDateTime timestamp;

    public Trade() {
        this.timestamp = LocalDateTime.now();
        this.status = PENDING_EXECUTION;
    }
}
