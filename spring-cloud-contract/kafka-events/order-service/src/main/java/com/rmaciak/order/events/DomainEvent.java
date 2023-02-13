package com.rmaciak.order.events;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public abstract class DomainEvent {

    public abstract UUID getEventId();
    public abstract Instant getOccurredAt();
    public abstract String getTopic();
}
