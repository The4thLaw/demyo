package org.demyo.web.velocity.tools;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.demyo.model.Series;
import org.junit.Test;

/**
 * Unit tests for {@link FormTool}.
 */
public class FormToolTest {
	private static final int NUM_MODELS = 5;
	private FormTool tool = new FormTool();

	/**
	 * Tests {@link FormTool#selectOptions(List, Object)} with selectable objects as models and with one possible
	 * choice.
	 */
	@Test
	public void testSingleSelectModel() {
		List<Series> possibleValues = new ArrayList<>(NUM_MODELS);
		for (int i = 1; i <= NUM_MODELS; i++) {
			possibleValues.add(createSeries(i));
		}

		String result = tool.selectOptions(possibleValues, "5");

		for (int i = 1; i < NUM_MODELS; i++) {
			assertThat(result).contains("<option value='" + i + "'>Series " + i + "</option>");
		}
		assertThat(result).contains("<option value='5'selected='selected'>Series 5</option>");
	}

	/**
	 * Tests {@link FormTool#selectOptions(List, Object)} with selectable objects as models and with multiple possible
	 * choices.
	 */
	@Test
	public void testMultipleSelectModel() {
		List<Series> possibleValues = new ArrayList<>(NUM_MODELS);
		for (int i = 1; i <= NUM_MODELS; i++) {
			possibleValues.add(createSeries(i));
		}

		int j = 0;
		String result = tool.selectOptions(possibleValues,
				Arrays.asList(possibleValues.get(j++), possibleValues.get(j++)));

		assertThat(result).contains("<option value='1'selected='selected'>Series 1</option>");
		assertThat(result).contains("<option value='2'selected='selected'>Series 2</option>");
		for (int i = ++j; i <= NUM_MODELS; i++) {
			assertThat(result).contains("<option value='" + i + "'>Series " + i + "</option>");
		}
	}

	/**
	 * Tests {@link FormTool#selectOptions(List, Object)} with selectable objects as a Map and with one possible choice.
	 */
	@Test
	public void testSingleSelectMap() {
		List<Map<?, ?>> possibleValues = new ArrayList<>(NUM_MODELS);
		for (int i = 1; i <= NUM_MODELS; i++) {
			possibleValues.add(createMap(i));
		}

		String result = tool.selectOptions(possibleValues, "id_5");

		for (int i = 1; i < NUM_MODELS; i++) {
			assertThat(result).contains("<option value='id_" + i + "'>Map " + i + "</option>");
		}
		assertThat(result).contains("<option value='id_5'selected='selected'>Map 5</option>");
	}

	private static Series createSeries(long id) {
		Series s = new Series();
		s.setId(id);
		s.setName("Series " + id);
		return s;
	}

	private static Map<String, String> createMap(long id) {
		Map<String, String> map = new HashMap<>();
		map.put("id", "id_" + id);
		map.put("identifyingName", "Map " + id);
		return map;
	}
}
