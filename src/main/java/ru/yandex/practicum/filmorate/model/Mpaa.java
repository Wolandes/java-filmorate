package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * MPAA.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Mpaa {
    private Long id;
    private String name;
}
