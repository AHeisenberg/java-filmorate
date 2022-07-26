package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class Event {

    @NotNull
    private final Long eventId;
    @NotNull
    private final Long userId;
    @NotNull
    private final Long entityId;
    @NotNull
    private final EventType eventType;
    @NotNull
    private final Operation operation;
    @NotNull
    private final Long timestamp;

    public enum EventType {
        LIKE,
        REVIEW,
        FRIEND
    }

    public enum Operation {
        REMOVE,
        ADD,
        UPDATE
    }
}
