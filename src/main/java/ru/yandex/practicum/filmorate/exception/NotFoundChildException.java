package ru.yandex.practicum.filmorate.exception;

public class NotFoundChildException extends RuntimeException {
    public NotFoundChildException(String message) {
        super(message);
    }
}
