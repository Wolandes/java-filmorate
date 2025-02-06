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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DirectorTest {
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
        Director director = Director.builder()
                .name("Test")
                .build();

        List<ConstraintViolation<Director>> violations = new ArrayList<>(validator.validate(director, ValidatorGroups.Update.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("id должен быть указан", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(director.toBuilder().id(1L).build(), ValidatorGroups.Update.class));
        assertTrue(violations.isEmpty(), "Валидация не пройдена");
    }

    @Test
    @DisplayName("Имя режиссера")
    public void shouldName() {
        Director director = Director.builder()
                .name(null)
                .build();

        List<ConstraintViolation<Director>> violations = new ArrayList<>(validator.validate(director, ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Имя не может быть пустым", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(director.toBuilder().name("").build(), ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Имя не может быть пустым", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(director.toBuilder().name("Name").build(), ValidatorGroups.Create.class));
        assertTrue(violations.isEmpty(), "Валидация не пройдена");
    }
}
