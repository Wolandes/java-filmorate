package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
public class ValidationService {

    private final int maxLengthDescription = 200;
    private final LocalDate minReleaseData = LocalDate.of(1895, 12, 28);
    private final char checkSymbol = '@';

    public Film checkValidationFilm(Film film) {
        log.info("Проверка имени");
        checkNameFilm(film.getName());
        log.info("Проверка длины");
        maxLengthDescription(film.getDescription());
        log.debug("Проверка минимальной даты");
        minDateReleaseDate(film.getReleaseDate());
        log.info("Проверка продолжительности");
        positiveDurationFilm(film.getDuration());
        log.info("Проверка на наличие действительности жанра");
        checkMpa(film.getMpa().getId());
        log.info("Проверка на наличии действительности возрастного рейтинга");
        checkGenre(film);
        return film;
    }

    public Film checkValidationFilmOnPut(Set<Long> allId, Film film) {
        if (allId.contains(film.getId())) {
            checkValidationFilm(film);
            return film;
        }
        throw new NotFoundException("Нет такого id в списке: " + film.getId());
    }

    public User checkValidationUser(User user) {
        checkEmail(user.getEmail());
        checkLogin(user.getLogin());
        String nameUser = checkName(user.getLogin(), user.getName());
        user.setName(nameUser);
        checkBirthday(user.getBirthday());
        return user;
    }

    public User checkValidationUserOnPut(Set<Long> allId, User user) {
        if (allId.contains(user.getId())) {
            checkValidationUser(user);
            return user;
        }
        throw new NotFoundException("Нет такого id в списке: " + user.getId());
    }

    public boolean checkNameFilm(String name) {
        if (name == null || name.isBlank()) {
            log.info("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        } else {
            return true;
        }
    }

    public boolean maxLengthDescription(String description) {
        if (description == null) {
            log.info("Нет описания фильма");
            return true;
        }
        if (description.length() > maxLengthDescription) {
            log.info("Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        } else {
            return true;
        }
    }

    public boolean minDateReleaseDate(LocalDate date) {
        if (date == null) {
            log.info("Не задано дата релиза");
            return true;
        }
        if (date.isBefore(minReleaseData)) {
            log.info("Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        } else {
            return true;
        }
    }

    public boolean positiveDurationFilm(int duration) {
        if (duration > 0) {
            return true;
        } else {
            log.info("Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    public boolean checkEmail(String email) {
        if (email == null || email.isBlank() || email.indexOf(checkSymbol) < 0) {
            log.info("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        return true;
    }

    public String checkName(String login, String name) {
        if (name == null || name.isBlank()) {
            return login;
        } else {
            return name;
        }
    }

    public boolean checkBirthday(LocalDate localDate) {
        if (localDate == null) {
            log.info("Не указано дата рождения пользователя");
            return true;
        }
        LocalDate localDateNow = LocalDate.now();
        if (localDateNow.isBefore(localDate)) {
            log.info("Год не может быть выше текущего года");
            throw new ValidationException("Дата рождения не может быть в будущем");
        } else {
            return true;
        }
    }

    public String checkLogin(String login) {
        if (login == null || login.isBlank()) {
            log.info("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        } else {
            return login;
        }
    }

    public boolean checkMpa(long id) {
        if (id == 0 || id > 5) {
            throw new ValidationException("Нет такого рейтинга с id: " + id);
        }
        return true;
    }

    public boolean checkGenre(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genre.getId() == 0 || genre.getId() > 6) {
                    throw new ValidationException("Нет такого жанра с id: " + genre.getId());
                }
            }
        }
        return true;
    }
}
