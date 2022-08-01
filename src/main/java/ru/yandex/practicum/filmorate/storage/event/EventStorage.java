package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {

    void addEvent(Long userId, Long entityId, Event.EventType eventType, Event.Operation operation);

    List<Event> showEventFeed(Long id);
}
