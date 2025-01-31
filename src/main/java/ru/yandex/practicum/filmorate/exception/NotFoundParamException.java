package ru.yandex.practicum.filmorate.exception;

public class NotFoundParamException extends RuntimeException {
    public NotFoundParamException(String message) {
        super(message);
    }
}
