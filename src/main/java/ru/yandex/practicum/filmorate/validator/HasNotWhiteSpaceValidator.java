package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.validator.annotation.HasNotWhiteSpace;

public class HasNotWhiteSpaceValidator implements ConstraintValidator<HasNotWhiteSpace, String> {
    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        return string == null || !string.contains(" ");
    }
}
