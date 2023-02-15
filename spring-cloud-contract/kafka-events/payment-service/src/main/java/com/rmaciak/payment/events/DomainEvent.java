package com.rmaciak.payment.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public abstract class DomainEvent {

    public abstract UUID getEventId();
    public abstract Instant getOccurredAt();
    public abstract String getTopic();
}
