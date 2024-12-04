package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFromValidator implements ConstraintValidator<DateAfter, LocalDate> {
    private LocalDate date;

    @Override
    public void initialize(DateAfter constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        date = LocalDate.parse(constraintAnnotation.date(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.isAfter(date);
        }
        return true;
    }
}
