package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.model.event.Event;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private static final String GET_FEED = """
            SELECT event_id, timestamp, user_id, event_type, operation, entity_id
            FROM feed
            WHERE user_id = :user_id""";

    private static final String INSERT_EVENT = """
            INSERT INTO feed (timestamp, user_id, event_type, operation, entity_id)
            VALUES (:timestamp, :user_id, :event_type, :operation, :entity_id)""";

    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<Event> mapper;

    @Override
    public List<Event> getFeed(Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        try {
            return jdbc.query(GET_FEED, params, mapper);
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public void createEvent(Event event) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("timestamp", event.getTimestamp());
        params.addValue("user_id", event.getUserId());
        params.addValue("event_type", event.getEventType().name());
        params.addValue("operation", event.getOperation().name());
        params.addValue("entity_id", event.getEntityId());
        try {
            jdbc.update(INSERT_EVENT, params);
        } catch (DataAccessException ignored) {
            throw new DbException(String.format(ExceptionMessages.INSERT_EVENT_ERROR, event.getEventId()));
        }
    }
}
