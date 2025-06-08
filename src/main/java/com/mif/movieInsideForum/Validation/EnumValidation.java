package com.mif.movieInsideForum.Validation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
@Documented
@Constraint(validatedBy = EnumValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValidation {
    Class<? extends Enum<?>> value(); // Enum class
    String message() default "Value must be one of the specified enum values";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}