package net.zjitc.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.zjitc.annotation.State;

public class RuleValidation implements ConstraintValidator<State, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        // 校验规则
        return "学生".equals(value) || "教师".equals(value) || "管理".equals(value);
    }
}
