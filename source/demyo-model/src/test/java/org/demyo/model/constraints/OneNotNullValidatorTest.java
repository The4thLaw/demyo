package org.demyo.model.constraints;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;

import org.demyo.model.Album;
import org.demyo.model.Derivative;
import org.demyo.model.DerivativeType;
import org.demyo.model.Series;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link OneNotNullValidator}.
 */
class OneNotNullValidatorTest {
	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void testValidationNegative() {
		Set<ConstraintViolation<Derivative>> violations;

		Derivative invalidDirectNull = new Derivative();
		invalidDirectNull.setType(new DerivativeType());
		violations = validator.validate(invalidDirectNull);
		assertThat(violations).withFailMessage("Unexpected number of violations").hasSize(2);

		Derivative invalidNestedNullBoth = new Derivative();
		invalidNestedNullBoth.setType(new DerivativeType());
		invalidNestedNullBoth.setAlbum(new Album());
		invalidNestedNullBoth.setSeries(new Series());
		violations = validator.validate(invalidNestedNullBoth);
		assertThat(violations).withFailMessage("Unexpected number of violations").hasSize(2);

		Derivative invalidNestedNullSeries = new Derivative();
		invalidNestedNullSeries.setType(new DerivativeType());
		invalidNestedNullSeries.setSeries(new Series());
		violations = validator.validate(invalidNestedNullSeries);
		assertThat(violations).withFailMessage("Unexpected number of violations").hasSize(2);

		Derivative invalidNestedNullAlbum = new Derivative();
		invalidNestedNullAlbum.setType(new DerivativeType());
		invalidNestedNullAlbum.setAlbum(new Album());
		violations = validator.validate(invalidNestedNullAlbum);
		assertThat(violations).withFailMessage("Unexpected number of violations").hasSize(2);
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
		assertThat(violations).withFailMessage("Unexpected number of violations").isEmpty();

		Derivative seriesSet = new Derivative();
		seriesSet.setType(new DerivativeType());
		seriesSet.setSeries(series);
		violations = validator.validate(seriesSet);
		assertThat(violations).withFailMessage("Unexpected number of violations").isEmpty();

		Derivative albumSet = new Derivative();
		albumSet.setType(new DerivativeType());
		albumSet.setAlbum(album);
		violations = validator.validate(albumSet);
		assertThat(violations).withFailMessage("Unexpected number of violations").isEmpty();
	}
}
