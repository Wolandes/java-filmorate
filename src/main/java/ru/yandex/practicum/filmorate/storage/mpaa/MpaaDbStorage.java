package ru.yandex.practicum.filmorate.storage.mpaa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.model.Mpaa;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaaDbStorage implements MpaaStorage {
    private static final String FIND_MPAA = "select id, name from public.mpaa where id = :id";
    private static final String ALL_MPAA = "select id, name from public.mpaa";

    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<Mpaa> mapper;

    @Override
    public Mpaa getMpaa(Long mpaaId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", mpaaId);
        try {
            return jdbc.queryForObject(FIND_MPAA, params, mapper);
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }

    @Override
    public List<Mpaa> getAllMpaa() {
        try {
            return jdbc.query(ALL_MPAA, new MapSqlParameterSource(), mapper);
        } catch (DataAccessException ignored) {
            throw new DbException(ExceptionMessages.SELECT_ERROR);
        }
    }
}
