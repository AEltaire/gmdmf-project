package net.zjitc.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import net.zjitc.validation.RuleValidation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {RuleValidation.class})
public @interface UserRule {

    // 失败提示信息
    String message() default "身份参数不正确";

    // 指定分组
    Class<?>[] groups() default {};

    // 负责信息
    Class<? extends Payload>[] payload() default {};
}
