<template>
	<div class="c-MetaSeriesIndex">
		<CardTextIndex
			:items="items" :first-letter-extractor="firstLetterExtractor"
			@page-change="$emit('page-change')"
		>
			<template #default="slotProps">
				<MetaSeriesCard :meta="slotProps.item" />
			</template>
		</CardTextIndex>
	</div>
</template>

<script>
import CardTextIndex from '@/components/CardTextIndex'
import MetaSeriesCard from '@/components/MetaSeriesCard'

export default {
	name: 'MetaSeriesIndex',

	components: {
		CardTextIndex,
		MetaSeriesCard
	},

	props: {
		items: {
			type: Array,
			required: true
		}
	},

	methods: {
		firstLetterExtractor(meta) {
			if (meta.series) {
				return meta.series.identifyingName[0]
			}
			if (meta.album) {
				return meta.album.title[0]
			}
			throw new Error('Item is neither a Series nor an Album: ' + JSON.stringify(meta))
		}
	}
}
</script>
