package ru.yandex.practicum.filmorate.model;

import lombok.*;

/**
 * Genre.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Genre {
    private Long id;
    private String name;
}
