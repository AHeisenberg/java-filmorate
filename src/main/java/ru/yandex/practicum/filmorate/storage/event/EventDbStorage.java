package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Repository
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String ADD_EVENT = "INSERT INTO events (user_id, entity_id, event_type, operation, timestamp)" +
            " values (?,?,?,?,?)";
    private static final String SHOW_EVENT_FEED = "SELECT * FROM events WHERE user_id = ?";

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addEvent(Long userId, Long entityId, Event.EventType eventType, Event.Operation operation) {
        jdbcTemplate.update(ADD_EVENT, userId, entityId, eventType.name(), operation.name(), Instant.now().toEpochMilli());
    }

    @Override
    public List<Event> showEventFeed(Long userId) {
        return jdbcTemplate.query(SHOW_EVENT_FEED, this::mapRowToEvent, userId);
    }

    private Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(resultSet.getLong("event_id"))
                .userId(resultSet.getLong("user_id"))
                .entityId(resultSet.getLong("entity_id"))
                .eventType(Event.EventType.valueOf(resultSet.getString("event_type")))
                .operation(Event.Operation.valueOf(resultSet.getString("operation")))
                .timestamp(resultSet.getLong("timestamp"))
                .build();
    }
}
