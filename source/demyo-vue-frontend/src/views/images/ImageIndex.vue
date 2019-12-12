<template>
	<div>
		<GalleryIndex :items="images">
			<template v-slot:default="slotProps">
				<router-link :to="`/authors/${slotProps.item.id}/view`">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</GalleryIndex>
		<v-btn
			fab to="/images/add" color="accent" fixed
			bottom right
		>
			<v-icon>mdi-plus</v-icon>
		</v-btn>
	</div>
</template>

<script>
import GalleryIndex from '@/components/GalleryIndex'
import imageService from '@/services/image-service'

export default {
	name: 'ImageIndex',

	components: {
		GalleryIndex
	},

	metaInfo() {
		return {
			title: this.$t('title.index.image')
		}
	},

	data() {
		return {
			images: []
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.$store.dispatch('ui/enableGlobalOverlay')
			this.images = await imageService.findForIndex()
			this.$store.dispatch('ui/disableGlobalOverlay')
		}
	}
}
</script>
