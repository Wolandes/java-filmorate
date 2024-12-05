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

public class UserTest {
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
        User user = User.builder()
                .email("test@yandex.ru")
                .login("test")
                .name("name")
                .birthday(LocalDate.of(2000, Month.JANUARY, 1))
                .build();

        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user, ValidatorGroups.Update.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("id должен быть указан", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(user.toBuilder().id(1L).build(), ValidatorGroups.Update.class));
        assertTrue(violations.isEmpty(), "Валидация не пройдена");
    }

    @Test
    @DisplayName("Электронная почта")
    public void shouldEmail() {
        User user = User.builder()
                .login("test")
                .name("name")
                .birthday(LocalDate.of(2000, Month.JANUARY, 1))
                .build();

        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user, ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Электронная почта не может быть пустой", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(user.toBuilder().email("").build(), ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Электронная почта не может быть пустой", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(user.toBuilder().email("yandex.ru").build(), ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Электронная почта не соответствует формату", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(user.toBuilder().email("test@yandex.ru").build(), ValidatorGroups.Create.class));
        assertTrue(violations.isEmpty(), "Валидация пройдена");
    }

    @Test
    @DisplayName("Логин")
    public void shouldLogin() {
        User user = User.builder()
                .email("test@yandex.ru")
                .name("name")
                .birthday(LocalDate.of(2000, Month.JANUARY, 1))
                .build();

        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user, ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Логин не может быть пустым", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(user.toBuilder().login("").build(), ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Логин не может быть пустым", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(user.toBuilder().login("te st").build(), ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Логин не может содержать пробелы", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(user.toBuilder().login("test").build(), ValidatorGroups.Create.class));
        assertTrue(violations.isEmpty(), "Валидация пройдена");
    }


    @Test
    @DisplayName("Дата рождения")
    public void shouldBirthday() {
        User user = User.builder()
                .email("test@yandex.ru")
                .login("test")
                .name("name")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        List<ConstraintViolation<User>> violations = new ArrayList<>(validator.validate(user, ValidatorGroups.Create.class));
        assertFalse(violations.isEmpty(), "Валидация пройдена");
        assertEquals("Дата рождения не может быть в будущем", violations.getFirst().getMessage());

        violations = new ArrayList<>(validator.validate(user.toBuilder().birthday(LocalDate.now()).build(), ValidatorGroups.Create.class));
        assertTrue(violations.isEmpty(), "Валидация пройдена");
    }
}
