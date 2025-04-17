package com.Kz.anno;
import com.Kz.validation.StateValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented     //元注解
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})      //元注解           表明注解的作用域，如类或者属性或者方法等..
@Retention(RetentionPolicy.RUNTIME)         //元注解           运行时生效
@Constraint(validatedBy = {StateValidation.class})              //指定校验规则的实现类
public @interface State {
    //提供校验失败后的信息
    String message() default "State只能为已发布或者草稿";

    //指定分组
    Class<?>[] groups() default {};

    //获取到State注解的附加信息（一般用不到）
    Class<? extends Payload>[] payload() default {}; 
}
