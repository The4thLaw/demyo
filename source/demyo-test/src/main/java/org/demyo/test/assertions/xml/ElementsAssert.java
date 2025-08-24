package org.demyo.test.assertions.xml;

import java.util.List;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ShouldHaveSize;
import org.assertj.core.internal.Failures;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * JSoup-based assertions for sets of XML nodes.
 */
public class ElementsAssert extends AbstractAssert<ElementsAssert, Elements> {
		private final Elements elements;

		public ElementsAssert(Elements elements) {
			super(elements, ElementsAssert.class);
			this.elements = elements;
		}

		public ElementsAssert hasSize(int expected) {
			if (elements.size() != expected) {
				throw Failures.instance().failure(info,
						ShouldHaveSize.shouldHaveSize(elements, elements.size(), expected));
			}
			return this;
		}

		public ElementAssert at(int index) {
			return new ElementAssert(elements.get(index));
		}

		public ElementAssert byId(int id) {
			return byId(String.valueOf(id));
		}

		public ElementAssert byId(String id) {
			List<Element> byId = elements.asList().stream().filter(e -> id.equals(e.attr("id"))).toList();

			if (byId.size() != 1) {
				throw Failures.instance().failure(info,
					new BasicErrorMessageFactory(String.format("%nExpected number of elements with ID %s: 1 but was: %d in:%n%s", id, byId.size(), "%s"), elements));
			}
			return new ElementAssert(byId.get(0));
		}
	}
