<template>
	<v-container class="c-ImageDetect">
		<SectionCard :subtitle="$t('title.add.image.detect')">
			<v-alert
				v-if="detectedImages.length > 0"
				border="start" type="info" variant="outlined"
			>
				{{ $t('page.Image.detect.recommendation') }}
			</v-alert>
			<v-alert
				v-if="!detecting && detectedImages.length === 0"
				border="start" type="warning" variant="outlined"
			>
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
			<GalleryIndex :items="addedImages" :keyboard-navigation="false">
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

<script setup lang="ts">
import imageService from '@/services/image-service'
import { useUiStore } from '@/stores/ui'
import { useHead } from '@unhead/vue'
import { useI18n } from 'vue-i18n'

const i18n = useI18n()
useHead({
	title: computed(() => i18n.t('title.add.image'))
})

const uiStore = useUiStore()

const detecting = ref(true)
const detectedImages = ref([] as string[])
// Iterating directly over this caused issues, possibly due to the special characters
// For example, the v-for wouldn't work (no iteration)
const imageSelections = ref({} as Record<string, boolean>)
const addedImages = ref([] as Image[])

async function fetchData(): Promise<void> {
	uiStore.enableGlobalOverlay()
	detectedImages.value = await imageService.detectDiskImages()
	imageSelections.value = {}
	detectedImages.value.forEach((v) => {
		imageSelections.value[v] = false
	})
	uiStore.disableGlobalOverlay()
}

async function save(): Promise<void> {
	uiStore.enableGlobalOverlay()

	addedImages.value = []

	const selectedImages: string[] = []
	for (const k in imageSelections.value) {
		if (imageSelections.value[k]) {
			selectedImages.push(k)
		}
	}
	if (selectedImages.length <= 0) {
		uiStore.disableGlobalOverlay()
		return
	}

	const added = await imageService.saveDiskImages(selectedImages)
	addedImages.value = await imageService.findMultipleById(added)
	await fetchData() // Will take care of hiding the overlay
}

void fetchData()
</script>

<style lang="scss">
.c-ImageDetect {
	.v-messages {
		display: none;
	}

	.v-label {
		word-break: break-all;
	}
}

.c-ImageDetect .v-input--selection-controls {
	margin-top: 0;
	padding-top: 16px;
}
</style>
