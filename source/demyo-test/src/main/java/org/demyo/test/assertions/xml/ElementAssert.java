package org.demyo.test.assertions.xml;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.StringAssert;
import org.assertj.core.api.WritableAssertionInfo;
import org.assertj.core.internal.Objects;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * JSoup-based assertions for single XML nodes.
 */
public class ElementAssert extends AbstractAssert<ElementAssert, Element> {
	private final Element element;

	public ElementAssert(Element element) {
		super(element, ElementAssert.class);
		this.element = element;
	}

	public ElementAssert exists() {
		objects.assertNotNull(info, element);
		return this;
	}

	public ElementAssert parent() {
		return new ElementAssert(element.parent());
	}

	public ElementsAssert children() {
		return new ElementsAssert(element.children());
	}

	public ElementsAssert siblings() {
		return new ElementsAssert(element.siblingElements());
	}

	public StringAssert source() {
		return new StringAssert(element.outerHtml());
	}

	public ElementsAssert tag(String tag) {
		Elements elements = element.getElementsByTag(tag);
		return new ElementsAssert(elements);
	}

	public ElementsAssert css(String css) {
		Elements elements = element.select(css);
		return new ElementsAssert(elements);
	}

	public ElementAssert cssSingle(String css) {
		Elements elements = element.select(css);
		return new ElementsAssert(elements).hasSize(1).at(0);
	}

	public ElementsAssert xpath(String xpath) {
		Elements elements = element.selectXpath(xpath);
		return new ElementsAssert(elements);
	}

	public ElementAssert xpathSingle(String xpath) {
		Elements elements = element.selectXpath(xpath);
		return new ElementsAssert(elements).hasSize(1).at(0);
	}

	public ElementAssert hasAttribute(String attrName, String attrValue) {
		String actualValue = element.attr(attrName);

		WritableAssertionInfo attrInfo = new WritableAssertionInfo();
		attrInfo.description("the attribute value for '%s' on %s", attrName, element);
		Objects.instance().assertNotNull(attrInfo, actualValue);
		Objects.instance().assertEqual(attrInfo, actualValue, attrValue);

		return this;
	}
}
