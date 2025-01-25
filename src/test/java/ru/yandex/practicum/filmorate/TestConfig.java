package ru.yandex.practicum.filmorate;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.repository.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.repository.mapper.UserRowMapper;

@TestConfiguration
public class TestConfig {

    @Bean
    public RowMapper<Film> filmRowMapper() {
        return new FilmRowMapper();
    }

    @Bean
    public RowMapper<Genre> genreRowMapper() {
        return new GenreRowMapper();
    }

    @Bean
    public RowMapper<Mpa> mpaRowMapper() {
        return new MpaRowMapper();
    }

    @Bean
    public RowMapper<User> userRowMapper() {
        return new UserRowMapper();
    }
}