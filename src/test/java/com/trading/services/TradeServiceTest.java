package com.trading.services;

import com.broker.external.BrokerTrade;
import com.broker.external.BrokerTradeSide;
import com.broker.external.ExternalBroker;
import com.trading.dto.TradeRequest;
import com.trading.exceptions.NotFoundException;
import com.trading.mappers.TradeMapper;
import com.trading.models.Trade;
import com.trading.models.TradeStatus;
import com.trading.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TradeServiceTest {

    @InjectMocks
    private TradeService tradeService;

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private ExternalBroker externalBroker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSubmitTrade() throws InterruptedException {
        TradeRequest tradeRequest = new TradeRequest("EUR/USD", 1000, BigDecimal.valueOf(1.123));
        Trade trade = TradeMapper.mapTradeRequestToTrade(tradeRequest, BrokerTradeSide.BUY);
        trade.setId(UUID.randomUUID());

        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        Trade submittedTrade = tradeService.submitTrade(trade);

        assertNotNull(submittedTrade);
        assertEquals(submittedTrade.getId(), trade.getId());
        assertEquals(submittedTrade.getSymbol(), trade.getSymbol());
        assertEquals(submittedTrade.getQuantity(), trade.getQuantity());
        assertEquals(submittedTrade.getSide(), trade.getSide());
        assertEquals(submittedTrade.getPrice(), trade.getPrice());
        assertEquals(submittedTrade.getStatus(), trade.getStatus());
        assertEquals(submittedTrade.getReason(), trade.getReason());
        assertEquals(submittedTrade.getTimestamp().toString(), trade.getTimestamp().toString());

        Thread.sleep(2000);

        verify(tradeRepository, times(1)).save(any(Trade.class));
        verify(externalBroker, times(1)).execute(any(BrokerTrade.class));
    }

    @Test
    void testGetTradeStatus() {
        UUID tradeId = UUID.randomUUID();
        Trade trade = new Trade();
        trade.setId(tradeId);
        trade.setStatus(TradeStatus.PENDING_EXECUTION);

        when(tradeRepository.findById(tradeId)).thenReturn(Optional.of(trade));

        Trade tradeDetails = tradeService.getTradeDetails(tradeId);

        assertNotNull(tradeDetails);
        assertEquals(TradeStatus.PENDING_EXECUTION, tradeDetails.getStatus());

        verify(tradeRepository, times(1)).findById(tradeId);
    }

    @Test
    void testGetTradeStatusNotFound() {
        UUID tradeId = UUID.randomUUID();

        when(tradeRepository.findById(tradeId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tradeService.getTradeDetails(tradeId));

        verify(tradeRepository, times(1)).findById(tradeId);
    }

    @Test
    void testGetTradeDetails() {
        UUID tradeId = UUID.randomUUID();
        Trade trade = new Trade();
        trade.setId(tradeId);
        trade.setSymbol("EUR/USD");
        trade.setQuantity(1000);
        trade.setSide(BrokerTradeSide.BUY);
        trade.setPrice(BigDecimal.valueOf(1.123));
        trade.setStatus(TradeStatus.PENDING_EXECUTION);
        trade.setTimestamp(LocalDateTime.now());

        when(tradeRepository.findById(tradeId)).thenReturn(Optional.of(trade));

        Trade tradeDetails = tradeService.getTradeDetails(tradeId);

        assertNotNull(tradeDetails);
        assertEquals(trade.getId(), tradeDetails.getId());
        assertEquals(trade.getSymbol(), tradeDetails.getSymbol());
        assertEquals(trade.getQuantity(), tradeDetails.getQuantity());
        assertEquals(trade.getSide(), tradeDetails.getSide());
        assertEquals(trade.getPrice(), tradeDetails.getPrice());
        assertEquals(trade.getStatus(), tradeDetails.getStatus());
        assertEquals(trade.getReason(), tradeDetails.getReason());
        assertEquals(trade.getTimestamp().toString(), tradeDetails.getTimestamp().toString());

        verify(tradeRepository, times(1)).findById(tradeId);
    }

    @Test
    void testGetTradeDetailsNotFound() {
        UUID tradeId = UUID.randomUUID();

        when(tradeRepository.findById(tradeId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tradeService.getTradeDetails(tradeId));

        verify(tradeRepository, times(1)).findById(tradeId);
    }

    @Test
    void testUpdateTradeStatus() {
        UUID tradeId = UUID.randomUUID();
        Trade trade = new Trade();
        trade.setId(tradeId);
        trade.setStatus(TradeStatus.PENDING_EXECUTION);

        when(tradeRepository.findById(tradeId)).thenReturn(Optional.of(trade));
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        tradeService.updateTradeStatus(tradeId, TradeStatus.EXECUTED, "Trade executed successfully");

        assertEquals(TradeStatus.EXECUTED, trade.getStatus());
        assertEquals("Trade executed successfully", trade.getReason());

        verify(tradeRepository, times(1)).findById(tradeId);
        verify(tradeRepository, times(1)).save(trade);
    }

    @Test
    void testUpdateTradeStatusNotFound() {
        UUID tradeId = UUID.randomUUID();

        when(tradeRepository.findById(tradeId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tradeService.updateTradeStatus(tradeId, TradeStatus.EXECUTED, "Trade executed successfully"));

        verify(tradeRepository, times(1)).findById(tradeId);
    }

    @Test
    void testGetAllTrades() {
        Trade trade1 = new Trade();
        trade1.setId(UUID.randomUUID());
        trade1.setSymbol("EUR/USD");
        trade1.setQuantity(1000);
        trade1.setSide(BrokerTradeSide.BUY);
        trade1.setPrice(BigDecimal.valueOf(1.123));
        trade1.setStatus(TradeStatus.PENDING_EXECUTION);
        trade1.setTimestamp(LocalDateTime.now());

        Trade trade2 = new Trade();
        trade2.setId(UUID.randomUUID());
        trade2.setSymbol("USD/JPY");
        trade2.setQuantity(10000000);
        trade2.setSide(BrokerTradeSide.SELL);
        trade2.setPrice(BigDecimal.valueOf(1.00));
        trade2.setStatus(TradeStatus.NOT_EXECUTED);
        trade2.setReason("No available quotes");
        trade2.setTimestamp(LocalDateTime.now());

        when(tradeRepository.findAll()).thenReturn(List.of(trade1, trade2));

        List<Trade> responseStream = tradeService.getAllTrades();

        assertNotNull(responseStream);
        assertEquals(2, responseStream.size());

        verify(tradeRepository, times(1)).findAll();
    }

    @Test
    void testTradeExpiry() throws InterruptedException {
        TradeRequest tradeRequest = new TradeRequest("EUR/USD", 1000, BigDecimal.valueOf(1.123));
        Trade trade = TradeMapper.mapTradeRequestToTrade(tradeRequest, BrokerTradeSide.BUY);
        trade.setId(UUID.randomUUID());

        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);
        when(tradeRepository.findById(trade.getId())).thenReturn(Optional.of(trade));

        tradeService.submitTrade(trade);

        Thread.sleep(61000);

        Trade tradeDetails = tradeService.getTradeDetails(trade.getId());
        assertEquals(TradeStatus.NOT_EXECUTED, tradeDetails.getStatus());
        assertEquals("Trade expired", trade.getReason());

        verify(tradeRepository, times(3)).findById(trade.getId());
        verify(tradeRepository, times(2)).save(trade);
    }
}
