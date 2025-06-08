package com.mif.movieInsideForum.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<EnumValidation, Enum<?>> {
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumValidation constraintAnnotation) {
        this.enumClass = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) return true; // Không kiểm tra nếu giá trị null
        return enumClass.isInstance(value);
    }
}
