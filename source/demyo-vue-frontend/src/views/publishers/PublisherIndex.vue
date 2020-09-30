<template>
	<div>
		<CardTextIndex
			:items="publishers" :first-letter-extractor="(item) => item.identifyingName[0]"
			@page-change="$emit('page-change')"
		>
			<template v-slot:default="slotProps">
				<PublisherCard :publisher="slotProps.item" />
			</template>
		</CardTextIndex>
		<v-btn
			fab to="/publishers/new" color="accent" fixed
			bottom right
		>
			<v-icon>mdi-plus</v-icon>
		</v-btn>
	</div>
</template>

<script>
import CardTextIndex from '@/components/CardTextIndex'
import PublisherCard from '@/components/PublisherCard'
import publisherService from '@/services/publisher-service'

export default {
	name: 'PublisherIndex',

	components: {
		CardTextIndex,
		PublisherCard
	},

	metaInfo() {
		return {
			title: this.$t('title.index.publisher')
		}
	},

	data() {
		return {
			publishers: []
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.$store.dispatch('ui/enableGlobalOverlay')
			this.publishers = await publisherService.findForIndex()
			this.$store.dispatch('ui/disableGlobalOverlay')
		}
	}
}
</script>
