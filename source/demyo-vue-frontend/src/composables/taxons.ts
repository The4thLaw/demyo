import { isProcessed } from '@/types/type-guards'
import type { CSSProperties } from 'vue'

/**
 * Gets the CSS style to apply for a taxon
 * @param taxon The taxon to use for the style
 * @returns The CSS style
 */
export function getStyle(taxon: ProcessedTaxon | Taxon | Partial<Taxon>): CSSProperties {
	const style: CSSProperties = {}
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
export function useTaxonStyle(taxon: Ref<Partial<Taxon>>): Ref<CSSProperties> {
	return computed(() => getStyle(taxon.value))
}

/**
 * Gets the maximum usage count for a set of taxons.
 * @param taxonsToCount The taxons to check
 * @param type The type of taxon
 * @returns The maximum.
 */
function getMaxUsageCount(taxonsToCount: Taxon[], type: TaxonType): number {
	if (taxonsToCount.length === 0) {
		return 0
	}

	const max = Math.max(...taxonsToCount.filter(t => t.type === type).map(t => t.usageCount))
	// Have a reasonable minimum else it just looks silly if there are few tags and they are seldom used
	return Math.max(max, 10)
}

export function postProcessTaxons(taxons: Taxon[]): ProcessedTaxon[] {
	const processed = taxons as ProcessedTaxon[]

	// Post-process taxons: compute the relative weight in %
	// (the base is 100% and the max is 200%, which is very convenient for the font-size)
	const maxUsageCount: Record<TaxonType, number> = {
		// eslint-disable-next-line @typescript-eslint/naming-convention
		GENRE: 0,
		// eslint-disable-next-line @typescript-eslint/naming-convention
		TAG: 0
	}

	maxUsageCount.GENRE = getMaxUsageCount(processed, 'GENRE')
	maxUsageCount.TAG = getMaxUsageCount(processed, 'TAG')

	for (const tag of processed) {
		tag.relativeWeight = Math.round(100 * tag.usageCount / maxUsageCount[tag.type] + 100)
	}

	return processed
}
