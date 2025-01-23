package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.DateAfter;
import ru.yandex.practicum.filmorate.validation.ValidatorGroups;

import java.time.LocalDate;
import java.util.LinkedHashSet;

/**
 * Film.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Film {
    @NotNull(groups = {ValidatorGroups.Update.class},
            message = "id должен быть указан")
    private Long id;
    @NotBlank(groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class},
            message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class},
            message = "Максимальная длина описания — 200 символов")
    private String description;
    @NotNull(groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class},
            message = "Дата релиза не может быть пустой")
    @DateAfter(date = "1895-12-27", groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class},
            message = "Дата релиза — не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Positive(groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class},
            message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
    @NotNull(groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class},
            message = "Возрастной рейтинг не может быть пустым")
    private Mpaa mpa;
    private LinkedHashSet<Genre> genres;
}
