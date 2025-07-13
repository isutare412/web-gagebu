package me.redshore.web_gagebu.feature.accountbook.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@NotNull(message = "Record description must not be null")
@Size(max = 512, message = "Record description must not exceed 512 characters")
public @interface RecordDescriptionValidation {

    String message() default "Invalid record description";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
