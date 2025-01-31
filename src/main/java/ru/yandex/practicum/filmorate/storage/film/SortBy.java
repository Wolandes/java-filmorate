package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundParamException;

public enum SortBy {
    YEAR,
    LIKES;

    public static SortBy from(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return null;
        }
        return switch (sortBy.toLowerCase().trim()) {
            case "year" -> YEAR;
            case "likes" -> LIKES;
            default -> throw new NotFoundParamException(
                    String.format(ExceptionMessages.PARAM_VALUE_FILMS_BY_DIRECTOR_ERROR, sortBy.toLowerCase().trim()));
        };
    }
}
