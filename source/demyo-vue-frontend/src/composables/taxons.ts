import { isProcessed } from '@/types/type-guards'

/**
 * Gets the CSS style to apply for a taxon
 * @param taxon The taxon to use for the style
 * @returns The CSS style
 */
export function getStyle(taxon: ProcessedTaxon | Taxon | Partial<Taxon>): Partial<CSSStyleDeclaration> {
	const style: Partial<CSSStyleDeclaration> = {}
	if (taxon.fgColour) {
		style.color = taxon.fgColour
	}
	if (taxon.bgColour) {
		style.backgroundColor = taxon.bgColour
	}
	if (isProcessed(taxon) && taxon.relativeWeight) {
		style.fontSize = taxon.relativeWeight + '%'
	}
	return style
}

/**
 * Gets the CSS style to apply for a taxon, as a reactive value.
 * @param taxon The taxon to use for the style
 * @returns The CSS style
 */
export function useTaxonStyle(taxon: Ref<Partial<Taxon>>): Ref<Partial<CSSStyleDeclaration>> {
	return computed(() => getStyle(taxon.value))
}
