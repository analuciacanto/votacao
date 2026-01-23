package br.com.softdesign.votacao.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFValidator implements ConstraintValidator<CPFValidation, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isBlank()) return false;

        // Remove pontos e traço
        cpf = cpf.replaceAll("[.-]", "");

        // Deve ter exatamente 11 dígitos
        return cpf.matches("\\d{11}");
    }
}
