package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.ExceptionMessages;
import ru.yandex.practicum.filmorate.exception.NotFoundParamException;

public enum SearchBy {
    DIRECTOR,
    TITLE;

    public static SearchBy from(String searchBy) {
        if (searchBy == null || searchBy.isBlank()) {
            return null;
        }
        return switch (searchBy.toLowerCase().trim()) {
            case "director" -> DIRECTOR;
            case "title" -> TITLE;
            default -> throw new NotFoundParamException(
                    String.format(ExceptionMessages.PARAM_VALUE_SEARCH_FILMS_ERROR, searchBy.toLowerCase().trim()));
        };
    }
}
