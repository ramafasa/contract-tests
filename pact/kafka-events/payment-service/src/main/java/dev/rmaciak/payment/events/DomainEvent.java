package dev.rmaciak.payment.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public abstract class DomainEvent {

    public abstract UUID getEventId();

    public abstract LocalDateTime getOccurredAt();

    public abstract String getTopic();
}
