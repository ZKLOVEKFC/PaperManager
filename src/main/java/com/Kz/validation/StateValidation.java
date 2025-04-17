package com.Kz.validation;

import com.Kz.anno.State;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

//          制定校验规则
public class StateValidation implements ConstraintValidator<State, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null)
            return false;           //校验失败

        if (s.equals("已发布") || s.equals("草稿"))
            return true;            //校验通过
        return false;
    }
}
