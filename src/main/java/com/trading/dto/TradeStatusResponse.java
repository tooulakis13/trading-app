package com.trading.dto;

import com.trading.models.TradeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeStatusResponse {
    private TradeStatus status;
}
