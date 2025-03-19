/**
 * Custom guard for processed tags
 */
function isProcessed(object: any): object is ProcessedTag {
    return 'relativeWeight' in object
}

/**
 * Gets the CSS style to apply for a tag
 * @param tag The tag to use for the style
 * @returns The CSS style
 */
export function getStyle(tag: ProcessedTag|Tag|Partial<Tag>): Partial<CSSStyleDeclaration> {
	const style: Partial<CSSStyleDeclaration> = {}
	if (tag.fgColour) {
		style.color = tag.fgColour
	}
	if (tag.bgColour) {
		style.backgroundColor = tag.bgColour
	}
	if (isProcessed(tag) && tag.relativeWeight) {
		style.fontSize = tag.relativeWeight + '%'
	}
	return style
}

/**
 * Gets the CSS style to apply for a tag, as a reactive value.
 * @param tag The tag to use for the style
 * @returns The CSS style
 */
export function useTagStyle(tag: Ref<Partial<Tag>>): Ref<Partial<CSSStyleDeclaration>> {
	return computed(() => getStyle(tag.value))
}
