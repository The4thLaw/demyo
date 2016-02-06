package org.demyo.model.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OneNotNullValidator.class)
@Documented
public @interface OneNotNull {
	String message() default "{org.demyo.model.constraints.onenotnull}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * @return The fields that cannot be <code>null</code> at the same time.
	 */
	String[] fields();
}
