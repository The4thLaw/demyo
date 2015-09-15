package org.demyo.model.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;

/**
 * The annotated String must match the format of an ISBN-10 or ISBN-13 number, with a potential revision number
 * like " / 001".
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
@Pattern(regexp = "^[0-9-]{10,}X?( ?/ ?[0-9]+)?$", flags = Flag.CASE_INSENSITIVE)
public @interface ISBN {
	/**
	 * @return The error message template.
	 */
	String message() default "{org.demyo.validation.constraints.ISBN.message}";
}
