package org.demyo.model.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Pattern.Flag;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated String must match the format of an ISBN-10 or ISBN-13 number, with a potential revision number
 * like " / 001".
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
@Pattern(regexp = "^[0-9-]{10,}X?( ?/ ?\\d+)?$", flags = Flag.CASE_INSENSITIVE)
public @interface ISBN {
	/**
	 * @return The error message template.
	 */
	String message() default "{org.demyo.validation.constraints.ISBN.message}";

	/**
	 * @return The groups the constraint belongs to.
	 */
	Class<?>[] groups() default {};

	/**
	 * @return The payload associated to the constraint
	 */
	Class<? extends Payload>[] payload() default {};
}
