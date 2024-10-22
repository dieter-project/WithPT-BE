package com.sideproject.withpt.application.notification.strategy;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class NotificationStrategyFactory {

    private final List<NotificationStrategy<?>> strategies;

    public NotificationStrategyFactory(List<NotificationStrategy<?>> strategies) {
        this.strategies = strategies;
    }

    public NotificationStrategy<?> getStrategy(Object relatedEntity) {
        return strategies.stream()
            .filter(strategy -> strategy.supports(relatedEntity))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No suitable strategy found for " + relatedEntity));
    }
}
