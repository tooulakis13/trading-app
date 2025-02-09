package com.trading.dto;

import com.broker.external.BrokerTradeSide;
import com.trading.models.Trade;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.trading.models.TradeStatus.PENDING_EXECUTION;

@Data
@AllArgsConstructor
public class TradeRequest {

    @NotNull
    @Pattern(regexp = "USD/JPY|EUR/USD", message = "Symbol valid values: USD/JPY, EUR/USD")
    private String symbol;

    @Min(value = 1, message = "Quantity must be greater than 0 and less than or equal to 1M")
    @Max(value = 1_000_000, message = "Quantity must be greater than 0 and less than or equal to 1M")
    private long quantity;

    @NotNull
    @DecimalMin(value = "0.0001", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
}
