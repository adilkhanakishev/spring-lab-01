package kz.iitu.springlab.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Individual Variant 2:
 * Class-level constraint: «A reprint year must not precede the first edition year».
 * Results in a global error reported by #fields.globalErrors().
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidReprintYearValidator.class)
@Documented
public @interface ValidReprintYear {
    String message() default "{book.reprintYear.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
