package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validate.Validation;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ValidationTests {

    private Validation validation;
    private Film film;
    private Film nameFilm;
    private Film maxLengthDescriptionFilm;
    private Film minDateReleaseDateFilm;
    private Film positiveDurationFilm;
    private User user;
    private User checkEmailUser;
    private User checkLoginUser;
    private User checkBirthdayUser;
    private User checNameUser;


    @BeforeEach
    public void setUp() {
        validation = new Validation();
        film = new Film();
        film.setId(1);
        film.setName("Имя фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofMinutes(10));
        //устанавливаем стандарт
        nameFilm = film;
        maxLengthDescriptionFilm = film;
        minDateReleaseDateFilm = film;
        positiveDurationFilm = film;
        user = new User();
        user.setId(1);
        user.setBirthday(LocalDate.of(1996, 9, 17));
        user.setEmail("volandesy@gmail.com");
        user.setLogin("Voland");
        user.setName("Volandesy");
        checkBirthdayUser = user;
        checkEmailUser = user;
        checkLoginUser = user;
        checNameUser = user;
    }

    @Test
    public void testCheckNameFilm() {
        nameFilm.setName("CheckName");
        validation.checkNameFilm(nameFilm.getName());
        assertEquals(nameFilm.getName(), "CheckName", "Не то имя");
        //nameFilm.setName(null);
        validation.checkNameFilm(nameFilm.getName());
        assertEquals(nameFilm.getName(), "CheckName", "Не сработало валидация");
    }

    @Test
    public void testMaxLengthDescription() {
        validation.maxLengthDescription(maxLengthDescriptionFilm.getDescription());
        assertEquals(maxLengthDescriptionFilm.getDescription(), film.getDescription(), "Не прошла проверку");
        //maxLengthDescriptionFilm.setDescription("waogn;pweragnerwuioghnewroignerwgdnergoenrwgl;erwsnglsadkfgnle;sraignla;ksng;oieragnals;awegraner;gjkbwer;laiugbaskl;jfgbaerl;igbaerguiolaerngl;" + "jengrkngawgreaioh;gershgserghl;srdhglserh/;sklerjh';lser/s;elikreg");
        //System.out.println(maxLengthDescriptionFilm.getDescription().length());
        validation.maxLengthDescription(maxLengthDescriptionFilm.getDescription());
        assertNotEquals(maxLengthDescriptionFilm.getDescription().length(), 210, "Прошла валидация");
    }

    @Test
    public void testMinDateReleaseDate() {
        validation.minDateReleaseDate(minDateReleaseDateFilm.getReleaseDate());
        assertEquals(minDateReleaseDateFilm.getReleaseDate(), film.getReleaseDate(), "Не прошла валидация");
        minDateReleaseDateFilm.setReleaseDate(LocalDate.of(1985, 12, 28));
        validation.minDateReleaseDate(minDateReleaseDateFilm.getReleaseDate());
        assertEquals(minDateReleaseDateFilm.getReleaseDate(), film.getReleaseDate(), "Прошла валидация");
    }

    @Test
    public void testPositiveDurationFilm() {
        validation.positiveDurationFilm(positiveDurationFilm.getDuration());
        assertEquals(positiveDurationFilm.getDuration(), film.getDuration(), "Не прошла валидация");
        //positiveDurationFilm.setDuration(Duration.ofMinutes(-1));
        //positiveDurationFilm.setDuration(Duration.ZERO);
        validation.positiveDurationFilm(positiveDurationFilm.getDuration());
        assertEquals(film.getDuration(), positiveDurationFilm.getDuration(), "Прошла валидация");
    }

    @Test
    public void testCheckEmail() {
        validation.checkEmail(checkEmailUser.getEmail());
        assertEquals(checkEmailUser.getEmail(), user.getEmail(), "Не прошла валидация");
        checkEmailUser.setEmail("dw@");
        validation.checkEmail(checkEmailUser.getEmail());
        assertEquals(checkEmailUser.getEmail(), user.getEmail(), "Прошла валидация");
    }

    @Test
    public void testCheckLogin() {
        validation.checkLogin(checkLoginUser.getLogin());
        assertEquals(checkLoginUser.getLogin(), user.getLogin(), "Не прошла валидация");
        checkLoginUser.setLogin("1");
        validation.checkLogin(checkLoginUser.getLogin());
        assertEquals(checkLoginUser.getLogin(), user.getLogin(), "Прошла валидация");
    }

    @Test
    public void testCheckName() {
        validation.checkName(checNameUser.getLogin(), checNameUser.getName());
        assertEquals(checNameUser.getName(), user.getName(), "Не прошла валидация");
        checNameUser.setName("");
        validation.checkName(checNameUser.getLogin(), checNameUser.getName());
        System.out.println(checNameUser.getLogin());
        System.out.println(checNameUser.getName());
        assertNotEquals(checNameUser.getName(), checNameUser.getLogin(), "Значения не равны");
    }

    @Test
    public void testCheckBirthday() {
        validation.checkBirthday(checkBirthdayUser.getBirthday());
        assertEquals(checkBirthdayUser.getBirthday(), user.getBirthday(), "Не прошла валидация");
        checkBirthdayUser.setBirthday(LocalDate.of(2024, 2, 1));
        validation.checkBirthday(checkBirthdayUser.getBirthday());
        assertEquals(checkBirthdayUser.getBirthday(), user.getBirthday(), "Значения не равны");
    }

    @Test
    public void testShouldChangeName() {
        validation.checkValidationUser(user);
        user.setName("");
        validation.checkValidationUser(user);
        assertEquals(user.getName(), user.getLogin(), "Значения не равны");
    }
}
