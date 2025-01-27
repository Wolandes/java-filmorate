package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpaa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaaRowMapper implements RowMapper<Mpaa> {
    @Override
    public Mpaa mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Mpaa(rs.getLong("id"), rs.getString("name"));
    }
}
