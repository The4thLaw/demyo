package org.demyo.model.constraints;

import java.lang.reflect.InvocationTargetException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom validator for {@link OneNotNull}.
 */
public class OneNotNullValidator implements ConstraintValidator<OneNotNull, Object> {
	private static final Logger LOGGER = LoggerFactory.getLogger(OneNotNullValidator.class);

	private String[] fieldNames;

	@Override
	public void initialize(final OneNotNull constraintAnnotation) {
		fieldNames = constraintAnnotation.fields();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		boolean foundOneNotNull = false;

		for (String field : fieldNames) {
			Object fieldValue = null;
			try {
				fieldValue = PropertyUtils.getNestedProperty(value, field);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				fieldValue = null;
				LOGGER.warn("Failed to get property {} on object; assuming null", field, e);
			} catch (NestedNullException ne) {
				// Support nested properties where part of the hierarchy is null
				fieldValue = null;
			}

			if (fieldValue != null) {
				foundOneNotNull = true;
				break;
			}
		}

		if (foundOneNotNull) {
			return true;
		}

		// Now, we need to add the errors on all fields
		for (String field : fieldNames) {
			context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
					.addPropertyNode(field).addConstraintViolation().disableDefaultConstraintViolation();
		}

		return false;
	}
}
