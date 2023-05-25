package com.cloudkeep.CloudKeep.utils.validator;

import com.cloudkeep.CloudKeep.utils.annotation.FieldMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
        final Object firstObj = beanWrapper.getPropertyValue(firstFieldName);
        final Object secondObj = beanWrapper.getPropertyValue(secondFieldName);

        return firstObj == null && secondObj == null || firstObj != null && firstObj.equals(secondObj);
    }
}
