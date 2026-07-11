package net.zjitc.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import net.zjitc.validation.StateValidation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {StateValidation.class})   // 指定校验规则类
public @interface State {

    // 失败提示信息
    String message() default "状态参数不正确";

    // 指定分组
    Class<?>[] groups() default {};

    // 负载信息
    Class<? extends Payload>[] payload() default {};
}
