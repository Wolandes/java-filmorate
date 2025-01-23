package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ValidatorGroups;

import java.time.LocalDate;

/**
 * User.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class User {
    @NotNull(groups = {ValidatorGroups.Update.class}, message = "id должен быть указан")
    private Long id;
    @NotBlank(groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class},
            message = "Электронная почта не может быть пустой")
    @Email(groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class},
            message = "Электронная почта не соответствует формату")
    private String email;
    @NotBlank(groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class},
            message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S*$", flags = {Pattern.Flag.UNICODE_CASE},
            groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class},
            message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class},
            message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
