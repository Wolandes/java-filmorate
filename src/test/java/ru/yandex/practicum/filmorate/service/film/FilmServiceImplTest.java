package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.exctractor.FilmGenreResultSetExtractor;
import ru.yandex.practicum.filmorate.exctractor.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.exctractor.FilmsResultSetExtractor;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.MpaaRowMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpaa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpaa.MpaaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmServiceImpl.class})
@ContextConfiguration(classes = {FilmDbStorage.class, FilmResultSetExtractor.class,
        FilmsResultSetExtractor.class, FilmGenreResultSetExtractor.class,
        UserDbStorage.class, UserRowMapper.class, MpaaDbStorage.class, MpaaRowMapper.class,
        GenreDbStorage.class, GenreRowMapper.class})
public class FilmServiceImplTest {
    private final FilmServiceImpl filmService;

    static Film getTestFilm() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(new Genre(2L, "Драма"));
        genres.add(new Genre(3L, "Мультфильм"));

        return new Film(1L,
                "test",
                "test description",
                LocalDate.of(2000, Month.JANUARY, 1),
                100,
                new Mpaa(1L, "G"),
                genres);
    }

    @Test
    @DisplayName("Поиск фильма по id")
    public void shouldGetFilm() {
        Film testFilm = getTestFilm();
        Optional<Film> userOptional = Optional.ofNullable(filmService.getFilm(testFilm.getId()));

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }

    @Test
    @DisplayName("Список фильмов")
    public void shouldGetAllFilms() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        List<Film> testFilm = new ArrayList<>();
        testFilm.add(getTestFilm());
        genres.add(new Genre(2L, "Драма"));
        testFilm.add(new Film(2L,
                "test2",
                "test description2",
                LocalDate.of(2001, Month.JANUARY, 1),
                110,
                new Mpaa(2L, "PG"),
                genres));
        genres = new LinkedHashSet<>();
        genres.add(new Genre(1L, "Комедия"));
        genres.add(new Genre(2L, "Драма"));
        genres.add(new Genre(3L, "Мультфильм"));
        testFilm.add(new Film(3L,
                "test3",
                "test description3",
                LocalDate.of(2002, Month.JANUARY, 1),
                120,
                new Mpaa(3L, "PG-13"),
                genres));
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
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(new Genre(2L, "Драма"));
        Film testFilm = getTestFilm().toBuilder()
                .id(4L)
                .name("test4")
                .description("test description4")
                .releaseDate(LocalDate.of(2004, Month.JANUARY, 1))
                .duration(80)
                .mpa(new Mpaa(4L, "R"))
                .genres(genres)
                .build();
        Optional<Film> filmOptional = Optional.ofNullable(filmService.createFilm(testFilm));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }

    @Test
    @DisplayName("Изменение фильма")
    public void shouldUpdateFilm() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(new Genre(2L, "Драма"));
        Film testFilm = getTestFilm().toBuilder()
                .id(1L)
                .name("test")
                .description("test description")
                .releaseDate(LocalDate.of(2005, Month.JANUARY, 1))
                .duration(100)
                .mpa(new Mpaa(2L, "PG"))
                .genres(genres)
                .build();
        Optional<Film> filmOptional = Optional.ofNullable(filmService.updateFilm(testFilm));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }

    @Test
    @DisplayName("Список популярных фильмов")
    public void shouldGetPopularFilms() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        List<Film> testFilm = new ArrayList<>();
        genres.add(new Genre(1L, "Комедия"));
        genres.add(new Genre(2L, "Драма"));
        genres.add(new Genre(3L, "Мультфильм"));
        testFilm.add(new Film(3L,
                "test3",
                "test description3",
                LocalDate.of(2002, Month.JANUARY, 1),
                120,
                new Mpaa(3L, "PG-13"),
                genres));
        testFilm.add(getTestFilm());
        filmService.addLike(3L, 1L);
        filmService.addLike(3L, 2L);
        Optional<List<Film>> filmOptional = Optional.ofNullable(filmService.getPopularFilms(2L));

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
        Optional<List<Film>> filmOptional = Optional.ofNullable(filmService.getPopularFilms(1L));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);

        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        testFilm.clear();
        genres.add(new Genre(1L, "Комедия"));
        genres.add(new Genre(2L, "Драма"));
        genres.add(new Genre(3L, "Мультфильм"));
        testFilm.add(new Film(3L,
                "test3",
                "test description3",
                LocalDate.of(2002, Month.JANUARY, 1),
                120,
                new Mpaa(3L, "PG-13"),
                genres));
        filmService.addLike(3L, 1L);
        filmService.addLike(3L, 2L);
        filmOptional = Optional.ofNullable(filmService.getPopularFilms(1L));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }

    @Test
    @DisplayName("Удалить лайк")
    public void shouldRemoveLike() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        List<Film> testFilm = new ArrayList<>();
        genres.add(new Genre(1L, "Комедия"));
        genres.add(new Genre(2L, "Драма"));
        genres.add(new Genre(3L, "Мультфильм"));
        testFilm.add(new Film(3L,
                "test3",
                "test description3",
                LocalDate.of(2002, Month.JANUARY, 1),
                120,
                new Mpaa(3L, "PG-13"),
                genres));
        filmService.addLike(3L, 1L);
        filmService.addLike(3L, 2L);
        Optional<List<Film>> filmOptional = Optional.ofNullable(filmService.getPopularFilms(1L));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);

        testFilm.clear();
        testFilm.add(getTestFilm());
        filmService.removeLike(3L, 1L);
        filmOptional = Optional.ofNullable(filmService.getPopularFilms(1L));

        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }
}
