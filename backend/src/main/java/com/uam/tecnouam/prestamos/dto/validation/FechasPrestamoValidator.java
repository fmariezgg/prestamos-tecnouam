package com.uam.tecnouam.prestamos.dto.validation;

import com.uam.tecnouam.prestamos.dto.PrestamoRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FechasPrestamoValidator implements ConstraintValidator<FechasPrestamoValidas, PrestamoRequest> {

    @Override
    public boolean isValid(PrestamoRequest request, ConstraintValidatorContext context) {
        // Null dates are reported by @NotNull on each field.
        if (request == null || request.fechaPrestamo() == null || request.fechaPrevistaDevolucion() == null) {
            return true;
        }
        if (!request.fechaPrevistaDevolucion().isBefore(request.fechaPrestamo())) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("fechaPrevistaDevolucion")
                .addConstraintViolation();
        return false;
    }
}
