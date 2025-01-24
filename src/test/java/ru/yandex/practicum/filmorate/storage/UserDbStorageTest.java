package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
@ContextConfiguration(classes = {UserRowMapper.class})
public class UserDbStorageTest {
    private final UserStorage userStorage;

    static User getTestUser() {
        return new User(1L,
                "test@yandex.ru",
                "test",
                "test name",
                LocalDate.of(2000, Month.JANUARY, 1));
    }

    @Test
    @DisplayName("Поиск пользователя по id")
    public void shouldGetUser() {
        User testUser = getTestUser();
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUser(testUser.getId()));

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testUser);
    }
}
