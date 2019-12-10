/**
 * Focuses a normally unfocusable element.
 * @param {Element} elem The element to focus
 */
export function focusElement(elem) {
	// Adding the negative tabindex makes the element focusable
	elem.setAttribute('tabindex', '-1')
	elem.focus()
}
