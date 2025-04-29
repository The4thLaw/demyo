package org.demyo.model;

import java.util.EnumSet;

import org.junit.jupiter.api.Test;

import org.demyo.model.enums.ModelField;

import static org.assertj.core.api.Assertions.assertThat;

class BookTypeTest {
	@Test
	void testFieldConfig() {
		BookType type = new BookType();

		type.setStructuredFieldConfig(EnumSet.of(ModelField.ALBUM_COLORIST, ModelField.ALBUM_INKER));
		assertThat(type.getFieldConfig()).isEqualTo("ALBUM_COLORIST,ALBUM_INKER");

		type.setFieldConfig(ModelField.ALBUM_TRANSLATOR.name());
		assertThat(type.getStructuredFieldConfig()).hasSameElementsAs(EnumSet.of(ModelField.ALBUM_TRANSLATOR));
	}
}
