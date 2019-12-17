<template>
	<v-container class="c-ImageDetect">
		<SectionCard :subtitle="$t('title.add.image.detect')">
			<v-alert border="left" type="info" text>
				{{ $t('page.Image.add.recommendation') }}
			</v-alert>
			<v-form>
				<v-checkbox
					v-for="(label, index) in detectedImages" :key="index"
					v-model="imageSelections[label]"
					:label="label"
				/>
			</v-form>
		</SectionCard>

		<FormActions :show-reset="false" @save="save" />
	</v-container>
</template>

<script>
import FormActions from '@/components/FormActions'
import SectionCard from '@/components/SectionCard'
import imageService from '@/services/image-service'

export default {
	name: 'ImageDetect',

	components: {
		FormActions,
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
			// Directly working with the paths as indexes caused issues, possibly due to the special characters
			// For example, the v-for wouldn't work (no iteration)
			imageSelections: {}
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.$store.dispatch('ui/enableGlobalOverlay')
			this.detectedImages = await imageService.detectDiskImages()
			this.detectedImages.forEach((v, i) => {
				this.imageSelections[v] = false
			})
			this.$store.dispatch('ui/disableGlobalOverlay')
		},

		save() {
			let selectedImages = []
			for (let k in this.imageSelections) {
				if (this.imageSelections[k]) {
					selectedImages.push(k)
				}
			}
			console.log(selectedImages)
			/*
			TODO:
			 - Save the images (with overlay until refresh)
			 - Show a confirmation toast with the right number of images
			 - Refresh the page
			 - Show a message if there are no detected images
			 - Hide the save button if there are no detected images
			*/
		}
	}
}
</script>

<style lang="less">
.c-ImageDetect .v-messages {
	display: none;
}

@media (min-width: 1264px) { // Same breakpoint as fluid container
	.c-ImageDetect .v-form {
		columns: 2;
	}
}

.c-ImageDetect .v-input--selection-controls {
	margin-top: 0;
	padding-top: 16px;
}
</style>
