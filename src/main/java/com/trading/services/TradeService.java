package com.trading.services;

import com.broker.external.BrokerTrade;
import com.broker.external.ExternalBroker;
import com.trading.events.TradeSubmissionEvent;
import com.trading.exceptions.NotFoundException;
import com.trading.models.Trade;
import com.trading.models.TradeStatus;
import com.trading.repository.TradeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.trading.models.TradeStatus.NOT_EXECUTED;
import static com.trading.models.TradeStatus.PENDING_EXECUTION;

@Service
@AllArgsConstructor
@Slf4j
public class TradeService {

    private final TradeRepository tradeRepository;

    private final ExternalBroker externalBroker;

    @EventListener
    public void handleTradeStatusEvent(TradeSubmissionEvent event) {
        log.debug("Handling trade status event for tradeId: {} with status: {} and reason: {}", event.getTradeId(), event.getStatus(), event.getReason());
        updateTradeStatus(event.getTradeId(), event.getStatus(), event.getReason());
    }

    public Trade submitTrade(Trade trade) {
        Trade savedTrade = tradeRepository.save(trade);

        CompletableFuture.runAsync(() -> {
            BrokerTrade brokerTrade = new BrokerTrade(savedTrade.getId(), savedTrade.getSymbol(),
                    savedTrade.getQuantity(), savedTrade.getSide(), savedTrade.getPrice());
            log.debug("Sending trade {} to external broker", savedTrade.getId());
            externalBroker.execute(brokerTrade);
        });

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            try {
                Trade latestTrade = tradeRepository.findById(savedTrade.getId()).orElse(null);
                if (latestTrade != null && latestTrade.getStatus() == PENDING_EXECUTION) {
                    if (LocalDateTime.now().minusSeconds(60).isAfter(latestTrade.getTimestamp())) {
                        log.warn("Trade {} expired, updating status to NOT_EXECUTED", latestTrade.getId());
                        updateTradeStatus(latestTrade.getId(), NOT_EXECUTED, "Trade expired");
                    }
                }
            } catch (Exception e) {
                log.error("Error during trade expiration check for trade {}: {}", savedTrade.getId(), e.getMessage(), e);
            }
        }, 60, TimeUnit.SECONDS);

        return savedTrade;
    }

    public Trade getTradeDetails(UUID tradeId) {
        return tradeRepository.findById(tradeId).orElseThrow(() -> new NotFoundException("Trade with id: " + tradeId + " not found"));
    }

    public void updateTradeStatus(UUID tradeId, TradeStatus status, String reason) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(() -> new NotFoundException("Trade with id: " + tradeId + " not found"));
        trade.setStatus(status);
        trade.setReason(reason);
        tradeRepository.save(trade);
        log.debug("Trade status updated for tradeId: {} to status: {} with reason: {}", tradeId, status, reason);
    }

    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }
}
