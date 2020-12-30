<template>
	<v-container class="c-ImageDetect">
		<SectionCard :subtitle="$t('title.add.image.detect')">
			<v-alert v-if="detectedImages.length > 0" border="left" type="info" text>
				{{ $t('page.Image.detect.recommendation') }}
			</v-alert>
			<v-alert v-if="!detecting && detectedImages.length === 0" border="left" type="warning" text>
				{{ $t('page.Image.detect.noImages') }}
			</v-alert>
			<v-form class="dem-columnized">
				<v-checkbox
					v-for="(label, index) in detectedImages" :key="index"
					v-model="imageSelections[label]"
					:label="label"
				/>
			</v-form>
		</SectionCard>

		<SectionCard v-if="addedImages.length" :subtitle="$t('page.Image.detect.lastBatch')">
			<GalleryIndex :items="addedImages">
				<template #default="slotProps">
					<router-link :to="`/images/${slotProps.item.id}/view`">
						{{ slotProps.item.identifyingName }}
					</router-link>
				</template>
			</GalleryIndex>
		</SectionCard>

		<FormActions v-if="detectedImages.length > 0" :show-reset="false" :show-back="false" @save="save" />
	</v-container>
</template>

<script>
import FormActions from '@/components/FormActions'
import GalleryIndex from '@/components/GalleryIndex'
import SectionCard from '@/components/SectionCard'
import imageService from '@/services/image-service'

export default {
	name: 'ImageDetect',

	components: {
		FormActions,
		GalleryIndex,
		SectionCard
	},

	metaInfo() {
		return {
			title: this.$t('title.add.image')
		}
	},

	data() {
		return {
			detecting: true,
			detectedImages: [],
			// Iterating directly over this caused issues, possibly due to the special characters
			// For example, the v-for wouldn't work (no iteration)
			imageSelections: {},
			addedImages: []
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.$store.dispatch('ui/enableGlobalOverlay')
			this.detectedImages = await imageService.detectDiskImages()
			this.imageSelections = {}
			this.detectedImages.forEach((v, i) => {
				this.imageSelections[v] = false
			})
			this.$store.dispatch('ui/disableGlobalOverlay')
		},

		async save() {
			this.$store.dispatch('ui/enableGlobalOverlay')

			this.addedImages = []

			const selectedImages = []
			for (const k in this.imageSelections) {
				if (this.imageSelections[k]) {
					selectedImages.push(k)
				}
			}
			if (selectedImages.length <= 0) {
				this.$store.dispatch('ui/disableGlobalOverlay')
				return
			}

			const added = await imageService.saveDiskImages(selectedImages)
			this.addedImages = await imageService.findMultipleById(added)
			this.fetchData() // Will take care of hiding the overlay
		}
	}
}
</script>

<style lang="less">
.c-ImageDetect .v-messages {
	display: none;
}

.c-ImageDetect .v-input--selection-controls {
	margin-top: 0;
	padding-top: 16px;
}
</style>
