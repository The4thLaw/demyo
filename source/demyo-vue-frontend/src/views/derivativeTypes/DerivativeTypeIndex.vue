<template>
	<div>
		<TextIndex :items="types" :split-by-first-letter="false">
			<template v-slot:default="slotProps">
				<router-link :to="`/derivativeTypes/${slotProps.item.id}/view`">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</TextIndex>
		<v-btn
			fab to="/derivativeTypes/new" color="accent" fixed
			bottom right
		>
			<v-icon>mdi-plus</v-icon>
		</v-btn>
	</div>
</template>

<script>
import TextIndex from '@/components/TextIndex'
import typeService from '@/services/derivative-type-service'

export default {
	name: 'DerivativeTypeIndex',

	components: {
		TextIndex
	},

	metaInfo() {
		return {
			title: this.$t('title.index.derivativeType')
		}
	},

	data() {
		return {
			types: []
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.$store.dispatch('ui/enableGlobalOverlay')
			this.types = await typeService.findForIndex()
			this.$store.dispatch('ui/disableGlobalOverlay')
		},

		scrollTop() {
			window.scroll(0, 0)
		}
	}
}
</script>
