package ru.yandex.practicum.filmorate.service.user;

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

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserServiceImpl.class})
@ContextConfiguration(classes = {UserDbStorage.class, UserRowMapper.class})
public class UserServiceImplTest {
    private final UserService userService;

    static User getTestUser() {
        return new User(1L,
                "test@yandex.ru",
                "test",
                "test name",
                LocalDate.of(2000, Month.JANUARY, 1));
    }

    @Test
    @DisplayName("Список пользователей")
    public void shouldGetAllUsers() {
        List<User> testUsers = new ArrayList<>();
        testUsers.add(getTestUser());
        testUsers.add(getTestUser().toBuilder()
                .id(2L)
                .email("test2@yandex.ru")
                .login("test2")
                .name("test name2")
                .birthday(LocalDate.of(2001, Month.JANUARY, 1))
                .build());
        testUsers.add(getTestUser().toBuilder()
                .id(3L)
                .email("test3@yandex.ru")
                .login("test3")
                .name("test name3")
                .birthday(LocalDate.of(2002, Month.JANUARY, 1))
                .build());
        Optional<List<User>> userOptional = Optional.ofNullable(userService.getAllUsers());

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testUsers);
    }

    @Test
    @DisplayName("Добавление пользователя")
    public void shouldCreateUser() {
        User userTest = getTestUser().toBuilder()
                .id(4L)
                .email("test4@yandex.ru")
                .login("test4")
                .name("test name4")
                .build();
        Optional<User> userOptional = Optional.ofNullable(userService.createUser(userTest));

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(userTest);
    }

    @Test
    @DisplayName("Изменение пользователя")
    public void shouldUpdateUser() {
        User userTest = getTestUser().toBuilder()
                .id(1L)
                .email("new-test@yandex.ru")
                .login("new-test")
                .name("new test name")
                .build();
        Optional<User> userOptional = Optional.ofNullable(userService.updateUser(userTest));

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(userTest);
    }

    @Test
    @DisplayName("Список друзей")
    public void shouldGetFriends() {
        List<User> userTest = new ArrayList<>();
        userTest.add(getTestUser().toBuilder()
                .id(3L)
                .email("test3@yandex.ru")
                .login("test3")
                .name("test name3")
                .birthday(LocalDate.of(2002, Month.JANUARY, 1))
                .build());
        Optional<List<User>> userOptional = Optional.ofNullable(userService.getFriends(2L));

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(userTest);
    }

    @Test
    @DisplayName("Список общих друзей")
    public void shouldGetFriendsCommonOther() {
        List<User> userTest = new ArrayList<>();
        userTest.add(getTestUser().toBuilder()
                .id(3L)
                .email("test3@yandex.ru")
                .login("test3")
                .name("test name3")
                .birthday(LocalDate.of(2002, Month.JANUARY, 1))
                .build());
        userService.addFriend(1L, 3L);
        Optional<List<User>> userOptional = Optional.ofNullable(userService.getFriendsCommonOther(1L, 2L));

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(userTest);
    }

    @Test
    @DisplayName("Добавление друга")
    public void shouldAddFriend() {
        List<User> userTest = new ArrayList<>();
        userTest.add(getTestUser());
        userTest.add(getTestUser().toBuilder()
                .id(3L)
                .email("test3@yandex.ru")
                .login("test3")
                .name("test name3")
                .birthday(LocalDate.of(2002, Month.JANUARY, 1))
                .build());
        Optional<List<User>> userOptional = Optional.ofNullable(userService.addFriend(2L, 1L));

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(userTest);
    }

    @Test
    @DisplayName("Удаление друга")
    public void shouldRemoveFriend() {
        List<User> userTest = new ArrayList<>();
        userService.removeFriend(2L, 3L);
        Optional<List<User>> userOptional = Optional.ofNullable(userService.getFriends(2L));

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(userTest);
    }
}
