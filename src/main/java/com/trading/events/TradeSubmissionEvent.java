package com.trading.events;

import com.trading.models.TradeStatus;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class TradeSubmissionEvent extends ApplicationEvent {
    private final UUID tradeId;
    private final TradeStatus status;
    private final String reason;

    public TradeSubmissionEvent(Object source, UUID tradeId, TradeStatus status, String reason) {
        super(source);
        this.tradeId = tradeId;
        this.status = status;
        this.reason = reason;
    }

    public UUID getTradeId() {
        return tradeId;
    }

    public TradeStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }
}