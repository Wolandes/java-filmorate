package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.ValidatorGroups;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @NotNull(groups = {ValidatorGroups.Update.class}, message = "id должен быть указан")
    private Long reviewId;

    @NotBlank(groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class},
            message = "Текст отзыва не должен быть пустым")
    private String content;

    @NotNull(groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class}, message = "id пользователя должен быть указан")
    private Long userId;

    @NotNull(groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class}, message = "id фильма должен быть указан")
    private Long filmId;

    @NotNull(groups = {ValidatorGroups.Create.class}, message = "тип отзыва (true/false) должен быть указан")
    private Boolean isPositive;

    private Integer useful = 0;
}
