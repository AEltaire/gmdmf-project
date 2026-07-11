package net.zjitc.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.zjitc.annotation.State;

public class StateValidation implements ConstraintValidator<State, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        // 校验规则
        return "待开课".equals(value) || "开课中".equals(value) || "已结束".equals(value);
    }
}
