package org.demyo.model.constraints;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;

import org.demyo.model.Album;
import org.demyo.model.Derivative;
import org.demyo.model.DerivativeType;
import org.demyo.model.Series;

/**
 * Tests for {@link OneNotNullValidator}.
 */
public class OneNotNullValidatorTest {
	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void testValidationNegative() {
		Set<ConstraintViolation<Derivative>> violations;

		Derivative invalidDirectNull = new Derivative();
		invalidDirectNull.setType(new DerivativeType());
		violations = validator.validate(invalidDirectNull);
		assertThat(violations.size()).withFailMessage("Unexpected number of violations").isEqualTo(2);

		Derivative invalidNestedNullBoth = new Derivative();
		invalidNestedNullBoth.setType(new DerivativeType());
		invalidNestedNullBoth.setAlbum(new Album());
		invalidNestedNullBoth.setSeries(new Series());
		violations = validator.validate(invalidNestedNullBoth);
		assertThat(violations.size()).withFailMessage("Unexpected number of violations").isEqualTo(2);

		Derivative invalidNestedNullSeries = new Derivative();
		invalidNestedNullSeries.setType(new DerivativeType());
		invalidNestedNullSeries.setSeries(new Series());
		violations = validator.validate(invalidNestedNullSeries);
		assertThat(violations.size()).withFailMessage("Unexpected number of violations").isEqualTo(2);

		Derivative invalidNestedNullAlbum = new Derivative();
		invalidNestedNullAlbum.setType(new DerivativeType());
		invalidNestedNullAlbum.setAlbum(new Album());
		violations = validator.validate(invalidNestedNullAlbum);
		assertThat(violations.size()).withFailMessage("Unexpected number of violations").isEqualTo(2);
	}

	@Test
	void testValidationPositive() {
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
		assertThat(violations.size()).withFailMessage("Unexpected number of violations").isZero();

		Derivative seriesSet = new Derivative();
		seriesSet.setType(new DerivativeType());
		seriesSet.setSeries(series);
		violations = validator.validate(seriesSet);
		assertThat(violations.size()).withFailMessage("Unexpected number of violations").isZero();

		Derivative albumSet = new Derivative();
		albumSet.setType(new DerivativeType());
		albumSet.setAlbum(album);
		violations = validator.validate(albumSet);
		assertThat(violations.size()).withFailMessage("Unexpected number of violations").isZero();
	}
}
