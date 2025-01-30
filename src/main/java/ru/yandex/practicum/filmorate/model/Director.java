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
public class Director {
    @NotNull(groups = {ValidatorGroups.Update.class}, message = "id должен быть указан")
    private Long id;
    @NotBlank(groups = {ValidatorGroups.Create.class, ValidatorGroups.Update.class},
            message = "Имя не может быть пустым")
    private String name;
}
