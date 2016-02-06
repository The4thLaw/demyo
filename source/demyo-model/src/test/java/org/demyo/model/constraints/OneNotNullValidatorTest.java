package org.demyo.model.constraints;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.demyo.model.Album;
import org.demyo.model.Derivative;
import org.demyo.model.DerivativeType;
import org.demyo.model.Series;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link OneNotNullValidator}.
 */
public class OneNotNullValidatorTest {
	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	public void testValidationNegative() {
		Set<ConstraintViolation<Derivative>> violations;

		Derivative invalidDirectNull = new Derivative();
		invalidDirectNull.setType(new DerivativeType());
		violations = validator.validate(invalidDirectNull);
		Assert.assertEquals("Unexpected number of violations", 2, violations.size());

		Derivative invalidNestedNullBoth = new Derivative();
		invalidNestedNullBoth.setType(new DerivativeType());
		invalidNestedNullBoth.setAlbum(new Album());
		invalidNestedNullBoth.setSeries(new Series());
		violations = validator.validate(invalidNestedNullBoth);
		Assert.assertEquals("Unexpected number of violations", 2, violations.size());

		Derivative invalidNestedNullSeries = new Derivative();
		invalidNestedNullSeries.setType(new DerivativeType());
		invalidNestedNullSeries.setSeries(new Series());
		violations = validator.validate(invalidNestedNullSeries);
		Assert.assertEquals("Unexpected number of violations", 2, violations.size());

		Derivative invalidNestedNullAlbum = new Derivative();
		invalidNestedNullAlbum.setType(new DerivativeType());
		invalidNestedNullAlbum.setAlbum(new Album());
		violations = validator.validate(invalidNestedNullAlbum);
		Assert.assertEquals("Unexpected number of violations", 2, violations.size());
	}

	@Test
	public void testValidationPositive() {
		Album album = new Album();
		album.setId(1L);

		Series series = new Series();
		series.setId(2L);

		Set<ConstraintViolation<Derivative>> violations;

		Derivative bothSet = new Derivative();
		bothSet.setType(new DerivativeType());
		bothSet.setAlbum(album);
		bothSet.setSeries(series);
		violations = validator.validate(bothSet);
		Assert.assertEquals("Unexpected number of violations", 0, violations.size());

		Derivative seriesSet = new Derivative();
		seriesSet.setType(new DerivativeType());
		seriesSet.setSeries(series);
		violations = validator.validate(seriesSet);
		Assert.assertEquals("Unexpected number of violations", 0, violations.size());

		Derivative albumSet = new Derivative();
		albumSet.setType(new DerivativeType());
		albumSet.setAlbum(album);
		violations = validator.validate(albumSet);
		Assert.assertEquals("Unexpected number of violations", 0, violations.size());
	}
}
