package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.repository.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.repository.JdbcMpaRepository;
import ru.yandex.practicum.filmorate.repository.JdbcUserRepository;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({JdbcFilmRepository.class, JdbcGenreRepository.class, JdbcMpaRepository.class, JdbcUserRepository.class, TestConfig.class})
public class FilmoRateApplicationTest {

    @Autowired
    private JdbcFilmRepository filmRepository;

    @Autowired
    private JdbcGenreRepository genreRepository;

    @Autowired
    private JdbcMpaRepository mpaRepository;

    @Autowired
    private JdbcUserRepository userRepository;

    @Test
    void testFindAllFilms() {
        Film film = new Film();
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setName("Test Film");
        film.setDescription("A test film description.");
        film.setReleaseDate(LocalDate.of(2025, 1, 18));
        film.setDuration(120);
        film.setMpa(mpa);
        Film savedFilm = filmRepository.addFilm(film);
        Collection<Film> films = filmRepository.getAllFilms();
        assertThat(films).isNotNull().isNotEmpty();
    }

    @Test
    void testAddAndFindFilm() {
        Film film = new Film();
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setName("Test Film");
        film.setDescription("A test film description.");
        film.setReleaseDate(LocalDate.of(2025, 1, 18));
        film.setDuration(120);
        film.setMpa(mpa);
        Film savedFilm = filmRepository.addFilm(film);
        assertThat(savedFilm.getId()).isNotNull();

        Film foundFilm = filmRepository.getFilm(savedFilm.getId());
        assertThat(foundFilm).isNotNull().hasFieldOrPropertyWithValue("name", "Test Film");
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film.setName("Old Name");
        film.setMpa(mpa);
        film.setDescription("Old Description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(90);
        Film savedFilm = filmRepository.addFilm(film);

        savedFilm.setName("Updated Name");
        savedFilm.setDescription("Updated Description");
        Film updatedFilm = filmRepository.updateFilm(savedFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Name");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void testFindAllGenres() {
        Collection<Genre> genres = genreRepository.getAllGenres();
        assertThat(genres).isNotNull().isNotEmpty();
    }

    @Test
    void testFindGenreById() {
        Genre genre = genreRepository.getGenre(1L);
        assertThat(genre).isNotNull().hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void testFindAllMpa() {
        Collection<Mpa> mpas = mpaRepository.getAllMpa();
        assertThat(mpas).isNotNull().isNotEmpty();
    }

    @Test
    void testFindMpaById() {
        Mpa mpa = mpaRepository.getMpa(1L);
        assertThat(mpa).isNotNull().hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void testAddAndFindUser() {
        User user = new User();
        user.setName("test User");
        user.setLogin("testUser");
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User savedUser = userRepository.addUser(user);
        assertThat(savedUser.getId()).isNotNull();

        User foundUser = userRepository.getUser(savedUser.getId());
        assertThat(foundUser).isNotNull().hasFieldOrPropertyWithValue("email", "test@example.com");
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setName("Old Name");
        user.setLogin("oldlogin");
        user.setEmail("old@example.com");
        user.setBirthday(LocalDate.of(1999, 1, 1));
        User savedUser = userRepository.addUser(user);

        savedUser.setLogin("newlogin");
        savedUser.setName("New Name");
        savedUser.setEmail("new@example.com");
        User updatedUser = userRepository.updateUser(savedUser);

        assertThat(updatedUser.getLogin()).isEqualTo("newlogin");
        assertThat(updatedUser.getName()).isEqualTo("New Name");
    }
}