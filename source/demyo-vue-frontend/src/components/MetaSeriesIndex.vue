<template>
	<div
		ref="keyTarget"
		v-touch="{
			left: nextPage,
			right: previousPage
		}"
		class="c-MetaSeriesIndex"
		@keyup.arrow-left.exact="previousPage()"
		@keyup.arrow-right.exact="nextPage()"
	>
		Hello, MetaSeriesIndex
	</div>
</template>

<script>
import { focusElement } from '@/helpers/dom'
import paginatedTextMixin from '@/mixins/paginated-text'

export default {
	name: 'MetaSeriesIndex',

	mixins: [paginatedTextMixin],

	mounted() {
		focusElement(this.$refs.keyTarget)
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
