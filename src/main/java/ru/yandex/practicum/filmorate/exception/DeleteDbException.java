package ru.yandex.practicum.filmorate.exception;

public class DeleteDbException extends RuntimeException {
    public DeleteDbException(String message) {
        super(message);
    }
}
