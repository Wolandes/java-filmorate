package ru.yandex.practicum.filmorate.service.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceImplTest {
    FilmStorage filmStorage;

    @BeforeEach
    public void init() {
        filmStorage = new InMemoryFilmStorage();
    }

    @Test
    @DisplayName("Обновление фильма")
    public void shouldUpdateFilm() {
        Film film = Film.builder().name("Test").releaseDate(LocalDate.now()).duration(10).build();

        Film newFilm = filmStorage.createFilm(film.toBuilder().build());

        Throwable throwable = assertThrows(NotFoundException.class, () -> filmStorage.updateFilm(film.toBuilder().id(0L).build()));
        assertEquals(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, 0L), throwable.getMessage(), "Текст сообщения не совпадает");

        assertDoesNotThrow(() -> filmStorage.updateFilm(newFilm.toBuilder().name("Film").build()));
        assertEquals("Film", filmStorage.getAllFilms().getFirst().getName(), "Не совпадают имена");
    }
}
