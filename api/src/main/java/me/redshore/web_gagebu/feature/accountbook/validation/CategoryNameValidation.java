package me.redshore.web_gagebu.feature.accountbook.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@NotBlank(message = "Category name must not be blank")
@Size(max = 16, message = "Category name must not exceed 16 characters")
public @interface CategoryNameValidation {

    String message() default "Invalid category name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
