package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {
    FilmService filmService;

    @BeforeEach
    public void init() {
        filmService = new FilmService();
    }

    @Test
    @DisplayName("Обновление фильма")
    public void shouldUpdateFilm() {
        Film film = Film.builder().name("Test").releaseDate(LocalDate.now()).duration(10).build();

        Film newFilm = filmService.createFilm(film.toBuilder().build());

        Throwable throwable = assertThrows(NotFoundException.class, () -> filmService.updateFilm(film.toBuilder().id(0L).build()));
        assertEquals(String.format(FilmService.FILM_NOT_FOUNT_ERROR, 0L), throwable.getMessage(), "Текст сообщения не совпадает");

        assertDoesNotThrow(() -> filmService.updateFilm(newFilm.toBuilder().name("Film").build()));
        assertEquals("Film", filmService.getAllFilms().getFirst().getName(), "Не совпадают имена");
    }
}
