<template>
	<ModelLink :model="model" :comma-separated="false" css-class="c-TaxonLink" view="TaxonView">
		<template #default="slotProps">
			<span :style="getStyle(slotProps.item)" class="d-Taxon">
				<template v-if="label">label</template>
				<template v-else>{{ slotProps.item.identifyingName }}</template>
				<span
					v-if="hasCount(slotProps.item)"
					class="d-Taxon__count" v-text="slotProps.item.usageCount"
				/>
			</span>
			<!-- Need to allow line breaks between taxons -->
			<wbr>
		</template>
	</ModelLink>
</template>

<script setup lang="ts">
import { getStyle } from '@/composables/taxons'

defineProps<{
	model: IModel
	label?: string
}>()

function hasCount(taxon: ProcessedTaxon): boolean {
	return taxon.usageCount >= 0
}
</script>

<style lang="scss">
.d-Taxon {
	border-top-left-radius: 6px;
	border-top-right-radius: 6px;
	border-bottom-right-radius: 6px;
	background-color: rgb(var(--v-theme-surface-light));
	margin-right: 1em;
	padding: 4px 12px;
	white-space: nowrap;
	line-height: 200%;

	h1 &,
	h2 &,
	h3 & {
		// Avoid overflows in titles containing taxons
		white-space: initial;
		line-height: 100%;
	}

	&__count {
		font-size: 80%;

		&::before {
			content: "(";
			margin-left: 0.5em;
		}

		&::after {
			content: ")";
		}
	}
}

a.c-TaxonLink:hover {
	text-decoration: none !important;
}
</style>
