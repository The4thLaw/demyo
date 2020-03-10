<template>
	<SectionCard :loading="loading" id="c-QuickSearchResults">
		Results: {{ results }}
	</SectionCard>
</template>

<script>
import SectionCard from '@/components/SectionCard'

export default {
	name: 'QuickSearchResults',

	components: {
		SectionCard
	},

	props: {
		results: {
			type: null,
			required: true
		},

		loading: {
			type: Boolean,
			default: false
		}
	}
}
</script>
