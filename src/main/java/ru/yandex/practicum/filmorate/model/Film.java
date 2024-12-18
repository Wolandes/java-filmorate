package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@EqualsAndHashCode
public class Film {

    long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
}
