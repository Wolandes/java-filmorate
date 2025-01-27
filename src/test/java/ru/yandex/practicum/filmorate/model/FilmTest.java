package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.validation.ValidatorGroups;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilmTest {
    ValidatorFactory validationFactory;
    Validator validator;

    @BeforeEach
    public void init() {
        validationFactory = Validation.buildDefaultValidatorFactory();
        validator = validationFactory.getValidator();
    }

    @AfterEach
    public void close() {
        validationFactory.close();
    }

    @Test
    @DisplayName("id")
    public void shouldId() {
        Film film = Film.builder()
                .name("Test")
                .releaseDate(LocalDate.now())
                .duration(10)
                .mpa(new Mpaa(1L, "G"))
                .build();

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film, ValidatorGroups.Update.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("id должен быть указан", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(film.toBuilder().id(1L).build(), ValidatorGroups.Update.class));
        assertTrue(violations.isEmpty(), "Валидация не пройдена");
    }

    @Test
    @DisplayName("Название фильма")
    public void shouldName() {
        Film film = Film.builder()
                .name(null)
                .releaseDate(LocalDate.now())
                .duration(10)
                .mpa(new Mpaa(1L, "G"))
                .build();

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film, ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Название не может быть пустым", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(film.toBuilder().name("").build(), ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Название не может быть пустым", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(film.toBuilder().name("Name").build(), ValidatorGroups.Create.class));
        assertTrue(violations.isEmpty(), "Валидация не пройдена");
    }

    @Test
    @DisplayName("Описание фильма")
    public void shouldDescription() {
        Film film = Film.builder()
                .name("Test")
                .description(".".repeat(201))
                .releaseDate(LocalDate.now())
                .duration(10)
                .mpa(new Mpaa(1L, "G"))
                .build();

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film, ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Максимальная длина описания — 200 символов", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(film.toBuilder().description(".".repeat(200)).build(), ValidatorGroups.Create.class));
        assertTrue(violations.isEmpty(), "Валидация не пройдена");
    }

    @Test
    @DisplayName("Дата релиза")
    public void shouldReleaseDate() {
        Film film = Film.builder()
                .name("Test")
                .duration(10)
                .mpa(new Mpaa(1L, "G"))
                .build();

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film, ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Дата релиза не может быть пустой", violations.getFirst().getMessage());

        film = film.toBuilder()
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 27))
                .build();
        violations = new ArrayList<>(validator.validate(film, ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", violations.getFirst().getMessage());

        film = film.toBuilder()
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 28))
                .build();
        violations = new ArrayList<>(validator.validate(film, ValidatorGroups.Create.class));
        assertTrue(violations.isEmpty(), "Валидация не пройдена");
    }

    @Test
    @DisplayName("Продолжительность фильма")
    public void shouldDuration() {
        Film film = Film.builder()
                .name("Test")
                .releaseDate(LocalDate.now())
                .duration(-1)
                .mpa(new Mpaa(1L, "G"))
                .build();

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film, ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Продолжительность фильма должна быть положительным числом", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(film.toBuilder().duration(1).build(), ValidatorGroups.Create.class));
        assertTrue(violations.isEmpty(), "Валидация не пройдена");
    }

    @Test
    @DisplayName("Возрастной рейтинг")
    public void shouldMpa() {
        Film film = Film.builder()
                .name("Test")
                .releaseDate(LocalDate.now())
                .duration(10)
                .mpa(null)
                .build();

        List<ConstraintViolation<Film>> violations = new ArrayList<>(validator.validate(film, ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Возрастной рейтинг не может быть пустым", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(
                film.toBuilder()
                        .mpa(new Mpaa(1L, "G"))
                        .build(),
                ValidatorGroups.Create.class));
        assertTrue(violations.isEmpty(), "Валидация не пройдена");
    }
}
