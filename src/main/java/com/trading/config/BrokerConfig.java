package com.trading.config;

import com.broker.external.BrokerResponseCallback;
import com.broker.external.ExternalBroker;
import com.trading.events.TradeSubmissionEvent;
import com.trading.models.TradeStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class BrokerConfig {

    @Bean
    public ExternalBroker externalBroker(ApplicationEventPublisher eventPublisher) {
        return new ExternalBroker(new BrokerResponseCallback() {
            @Override
            public void successful(UUID tradeId) {
                eventPublisher.publishEvent(new TradeSubmissionEvent(this, tradeId, TradeStatus.EXECUTED, null));
            }

            @Override
            public void unsuccessful(UUID tradeId, String reason) {
                eventPublisher.publishEvent(new TradeSubmissionEvent(this, tradeId, TradeStatus.NOT_EXECUTED, reason));
            }
        });
    }
}
