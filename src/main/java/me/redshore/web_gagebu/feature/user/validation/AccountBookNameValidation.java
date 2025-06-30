package me.redshore.web_gagebu.feature.user.validation;

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
@NotBlank(message = "Account book name must not be blank")
@Size(max = 64, message = "Account book name must not exceed 64 characters")
public @interface AccountBookNameValidation {

    String message() default "Invalid account book name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
