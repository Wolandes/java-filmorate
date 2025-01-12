package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
    Set<Long> idUserLikes;
    List<Integer> genres_id;
    int mpa_id;
}
