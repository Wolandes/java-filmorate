package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpaa;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpaa.MpaaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmServiceImpl.class})
@ContextConfiguration(classes = {FilmDbStorage.class, FilmRowMapper.class,
        UserDbStorage.class, UserRowMapper.class, MpaaDbStorage.class, MpaaRowMapper.class,
        GenreDbStorage.class, GenreRowMapper.class, DirectorDbStorage.class, DirectorRowMapper.class})
public class FilmServiceImplTest {
    private final FilmServiceImpl filmService;

    static Film getTestFilm() {
        Set<Genre> genres = new LinkedHashSet<>();
        genres.add(new Genre(2L, "Драма"));
        genres.add(new Genre(3L, "Мультфильм"));

        Set<Director> directors = new LinkedHashSet<>();
        directors.add(new Director(1L, "test director"));
        directors.add(new Director(2L, "test director2"));

        return new Film(1L,
                "test",
                "test description",
                LocalDate.of(2000, Month.JANUARY, 1),
                100,
                new Mpaa(1L, "G"),
                genres,
                directors);
    }

    @Test
    @DisplayName("Поиск фильма по id")
    public void shouldGetFilm() {
        Film testFilm = getTestFilm();
        Optional<Film> filmOptional = Optional.ofNullable(filmService.getFilm(testFilm.getId()));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }

    @Test
    @DisplayName("Список фильмов")
    public void shouldGetAllFilms() {
        List<Film> testFilm = new ArrayList<>();
        testFilm.add(getTestFilm());
        testFilm.add(filmService.getFilm(2L));
        testFilm.add(filmService.getFilm(3L));
        testFilm.add(filmService.getFilm(4L));
        Optional<List<Film>> filmOptional = Optional.ofNullable(filmService.getAllFilms());

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }

    @Test
    @DisplayName("Добавление фильма")
    public void shouldCreateFilm() {
        Set<Genre> genres = new LinkedHashSet<>();
        genres.add(new Genre(2L, "Драма"));
        Set<Director> directors = new LinkedHashSet<>();
        directors.add(new Director(3L, "test director3"));
        Film testFilm = getTestFilm().toBuilder()
                .name("new test")
                .description("new test description")
                .releaseDate(LocalDate.of(2024, Month.JANUARY, 1))
                .duration(80)
                .mpa(new Mpaa(4L, "R"))
                .genres(genres)
                .directors(directors)
                .build();
        Optional<Film> filmOptional = Optional.ofNullable(filmService.createFilm(testFilm));
        testFilm.setId(filmOptional.isPresent() ? filmOptional.get().getId() : 0L);

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }

    @Test
    @DisplayName("Изменение фильма")
    public void shouldUpdateFilm() {
        Set<Genre> genres = new LinkedHashSet<>();
        genres.add(new Genre(2L, "Драма"));
        Set<Director> directors = new LinkedHashSet<>();
        directors.add(new Director(1L, "test director"));
        Film testFilm = getTestFilm().toBuilder()
                .releaseDate(LocalDate.of(2005, Month.JANUARY, 1))
                .mpa(new Mpaa(2L, "PG"))
                .genres(genres)
                .directors(directors)
                .build();
        Optional<Film> filmOptional = Optional.ofNullable(filmService.updateFilm(testFilm));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }

    @Test
    @DisplayName("Удаление фильма")
    public void shouldRemoveFilm() {
        Long filmId = getTestFilm().getId();
        filmService.removeFilm(filmId);

        assertThrows(NotFoundException.class,
                () -> filmService.getFilm(filmId));
    }

    @Test
    @DisplayName("Список популярных фильмов")
    public void shouldGetPopularFilms() {
        List<Film> testFilm = new ArrayList<>();
        testFilm.add(filmService.getFilm(3L));
        testFilm.add(getTestFilm());
        filmService.addLike(3L, 1L);
        filmService.addLike(3L, 2L);
        Optional<List<Film>> filmOptional = Optional.ofNullable(filmService.getPopularFilms(2L, null, null));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }

    @Test
    @DisplayName("Добавить лайк")
    public void shouldAddLike() {
        List<Film> testFilm = new ArrayList<>();
        testFilm.add(getTestFilm());
        Optional<List<Film>> filmOptional = Optional.ofNullable(filmService.getPopularFilms(1L, null, null));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);

        testFilm.clear();
        testFilm.add(filmService.getFilm(3L));
        filmService.addLike(3L, 1L);
        filmService.addLike(3L, 2L);
        filmOptional = Optional.ofNullable(filmService.getPopularFilms(1L, null, null));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }

    @Test
    @DisplayName("Удалить лайк")
    public void shouldRemoveLike() {
        List<Film> testFilm = new ArrayList<>();
        testFilm.add(filmService.getFilm(3L));
        filmService.addLike(3L, 1L);
        filmService.addLike(3L, 2L);
        Optional<List<Film>> filmOptional = Optional.ofNullable(filmService.getPopularFilms(1L, null, null));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);

        testFilm.clear();
        testFilm.add(getTestFilm());
        filmService.removeLike(3L, 1L);
        filmOptional = Optional.ofNullable(filmService.getPopularFilms(1L, null, null));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }

    @Test
    @DisplayName("Список фильмов по режиссеру с сортировкой")
    public void shouldGetFilmsByDirectorId() {
        List<Film> testFilm = new ArrayList<>();
        testFilm.add(filmService.getFilm(1L));
        testFilm.add(filmService.getFilm(3L));
        Optional<List<Film>> filmOptional = Optional.ofNullable(filmService.getFilmsByDirectorId(1L, "year"));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);

        testFilm.clear();
        testFilm.add(filmService.getFilm(3L));
        testFilm.add(filmService.getFilm(1L));
        filmService.addLike(3L, 1L);
        filmService.addLike(3L, 2L);
        filmOptional = Optional.ofNullable(filmService.getFilmsByDirectorId(1L, "likes"));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }

    @Test
    @DisplayName("Поиск фильмов")
    public void shouldSearchFilms() {
        List<Film> testFilm = new ArrayList<>();
        testFilm.add(filmService.getFilm(2L));
        Optional<List<Film>> filmOptional = Optional.ofNullable(filmService.searchFilms("2", "title"));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);

        filmOptional = Optional.ofNullable(filmService.searchFilms("4", "director"));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);

        filmOptional = Optional.ofNullable(filmService.searchFilms("3", "title"));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isNotEqualTo(testFilm);

        filmOptional = Optional.ofNullable(filmService.searchFilms("2", "director"));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isNotEqualTo(testFilm);

        testFilm.clear();
        testFilm.add(filmService.getFilm(3L));
        testFilm.add(filmService.getFilm(4L));

        filmOptional = Optional.ofNullable(filmService.searchFilms("director3", "director,title"));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);

        testFilm.remove(0);

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isNotEqualTo(testFilm);

    }
}
