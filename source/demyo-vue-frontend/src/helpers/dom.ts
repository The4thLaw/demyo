/**
 * Focuses a normally unfocusable element.
 * @param elem The element to focus
 */
export function focusElement(elem: HTMLElement | null): void {
	if (!elem) {
		return
	}

	// Adding the negative tabindex makes the element focusable
	elem.setAttribute('tabindex', '-1')
	elem.focus()
}
