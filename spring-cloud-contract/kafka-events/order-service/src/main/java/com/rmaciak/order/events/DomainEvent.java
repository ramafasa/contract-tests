package com.rmaciak.order.events;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public abstract class DomainEvent {

    public abstract UUID getEventId();
    public abstract LocalDateTime getOccurredAt();
    public abstract String getTopic();
}
