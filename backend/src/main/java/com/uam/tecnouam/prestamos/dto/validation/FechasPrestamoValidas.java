package com.uam.tecnouam.prestamos.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Class-level rule: fechaPrevistaDevolucion must not be before fechaPrestamo. */
@Documented
@Constraint(validatedBy = FechasPrestamoValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FechasPrestamoValidas {

    String message() default "La fecha prevista de devolución no puede ser anterior a la fecha de préstamo";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
