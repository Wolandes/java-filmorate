package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    public void init() {
        filmController = new FilmController();
    }

    @Test
    @DisplayName("Название фильма")
    public void shouldName() {
        final Film film = Film.builder()
                .name(null)
                .releaseDate(LocalDate.now())
                .duration(10)
                .build();

        Throwable throwable = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals(FilmController.NAME_VALIDATION_ERROR, throwable.getMessage(), "Текст сообщения не совпадает");

        throwable = assertThrows(ValidationException.class, () -> filmController.createFilm(film.toBuilder().name("").build()));
        assertEquals(FilmController.NAME_VALIDATION_ERROR, throwable.getMessage(), "Текст сообщения не совпадает");

        assertDoesNotThrow(() -> filmController.createFilm(film.toBuilder().name("Test").build()));
        assertEquals("Test", filmController.getAllFilms().getFirst().getName(), "Не совпадают имена");
    }

    @Test
    @DisplayName("Описание фильма")
    public void shouldDescription() {
        Film film = Film.builder()
                .name("Test")
                .description(".".repeat(201))
                .releaseDate(LocalDate.now())
                .duration(10)
                .build();

        Throwable throwable = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals(FilmController.DESCRIPTION_VALIDATION_ERROR, throwable.getMessage(), "Текст сообщения не совпадает");

        assertDoesNotThrow(() -> filmController.createFilm(film.toBuilder().description(".".repeat(200)).build()));
        assertEquals(".".repeat(200), filmController.getAllFilms().getFirst().getDescription(), "Не совпадают описания");
    }

    @Test
    @DisplayName("Дата релиза")
    public void shouldReleaseDate() {
        Film film = Film.builder()
                .name("Test")
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 27))
                .duration(10)
                .build();

        Throwable throwable = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals(FilmController.RELEASE_DATE_VALIDATION_ERROR, throwable.getMessage(), "Текст сообщения не совпадает");

        assertDoesNotThrow(() -> filmController.createFilm(film.toBuilder().releaseDate(LocalDate.of(1895, Month.DECEMBER, 28)).build()));
        assertEquals(LocalDate.of(1895, Month.DECEMBER, 28), filmController.getAllFilms().getFirst().getReleaseDate(), "Не совпадают даты релиза");
    }

    @Test
    @DisplayName("Продолжительность фильма")
    public void shouldDuration() {
        Film film = Film.builder()
                .name("Test")
                .releaseDate(LocalDate.now())
                .duration(-10)
                .build();

        Throwable throwable = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals(FilmController.DURATION_VALIDATION_ERROR, throwable.getMessage(), "Текст сообщения не совпадает");

        assertDoesNotThrow(() -> filmController.createFilm(film.toBuilder().duration(10).build()));
        assertEquals(10, filmController.getAllFilms().getFirst().getDuration(), "Не совпадают продолжительность фильмов");
    }

    @Test
    @DisplayName("Обновление фильма")
    public void shouldUpdateFilm() {
        Film film = Film.builder()
                .name("Test")
                .releaseDate(LocalDate.now())
                .duration(10)
                .build();
        Film newFilm = filmController.createFilm(film.toBuilder().build());

        Throwable throwable = assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
        assertEquals(FilmController.FILMID_VALIDATION_ERROR, throwable.getMessage(), "Текст сообщения не совпадает");

        throwable = assertThrows(NotFoundException.class, () -> filmController.updateFilm(film.toBuilder().id(0L).build()));
        assertEquals(String.format(FilmController.FILM_NOT_FOUND_ERROR, 0L), throwable.getMessage(), "Текст сообщения не совпадает");

        assertDoesNotThrow(() -> filmController.updateFilm(newFilm.toBuilder().name("Film").build()));
        assertEquals("Film", filmController.getAllFilms().getFirst().getName(), "Не совпадают имена");
    }
}
