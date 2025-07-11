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
@NotBlank(message = "Record summary must not be blank")
@Size(max = 32, message = "Record summary must not exceed 32 characters")
public @interface RecordSummaryValidation {

    String message() default "Invalid record summary";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
