<template>
	<div
		v-if="items"
		ref="key-target"
		v-touch="{
			left: nextPage,
			right: previousPage
		}"
		class="c-GalleryIndex"
		@keyup.arrow-left.exact="previousPageKeyboard()"
		@keyup.arrow-right.exact="nextPageKeyboard()"
	>
		<div class="c-GalleryIndex__list">
			<v-sheet v-for="item in paginatedItems" :key="item.id" class="c-GalleryIndex__image" :border="bordered">
				<ImageThumb :image="item.thumbImage" />
				<legend v-if="hasDefaultSlot" class="c-GalleryIndex__imageLegend">
					<slot :item="item" />
				</legend>
			</v-sheet>
		</div>

		<v-pagination
			v-if="pageCount > 1"
			v-model="currentPage"
			:length="pageCount"
			total-visible="10"
			class="my-2"
			@update:model-value="emit('page-change')"
		/>
	</div>
</template>

<script setup lang="ts" generic="T extends IModel">
import { emitTypes } from '@/composables/pagination'
import { focusElement } from '@/helpers/dom'
import { useReaderStore } from '@/stores/reader'
import { isImage } from '@/types/type-guards'
import { useTemplateRef } from 'vue'

interface ResolvedImage {
	thumbImage?: Image
}

const props = withDefaults(defineProps<{
	items: T[]
	imagePath?: keyof T
	bordered?: boolean
	keyboardNavigation?: boolean
}>(), {
	imagePath: undefined,
	bordered: true,
	keyboardNavigation: true
})

const emit = defineEmits(emitTypes)

const lightboxOpened = ref(false)
let observer: MutationObserver | null = null
const currentPage = ref(1)

const keyTarget = useTemplateRef('key-target')

const readerStore = useReaderStore()
const itemsPerPage = computed(() => readerStore.currentReader.configuration.pageSizeForImages)

const slots = useSlots()
const hasDefaultSlot = computed(() => !!slots.default)

const pageCount = computed(() => Math.ceil(props.items.length / itemsPerPage.value))

const paginatedItems = computed(() => {
	const slice = props.items.slice((currentPage.value - 1) * itemsPerPage.value,
		currentPage.value * itemsPerPage.value)
	return slice.map(item => {
		const processedItem: ResolvedImage & T = item
		let image: Image
		if (props.imagePath) {
			image = item[props.imagePath] as Image
		} else if (isImage(item)) {
			image = item
		} else {
			throw new Error(`${JSON.stringify(item)} isn't an image and no imagePath was provided`)
		}
		if (image) { // Some entries may not have an image at all
			processedItem.thumbImage = image
		}
		return processedItem
	})
})

function focusKeyboardNav(): void {
	if (props.keyboardNavigation) {
		// Focus may cause the browser to scroll to the element. It's fine if the element is the main one on the
		// page but causes issues once it's further down, like in the AlbumView
		focusElement(keyTarget.value)
	}
}

onMounted(() => {
	focusKeyboardNav()

	// Monitor the lightbox plugin DOM since we don't have callacks
	const targetNode = document.getElementById('demyo')
	if (!targetNode) {
		console.log("Can't monitor the status of the lightbox, the target node is missing")
		return
	}
	const callback: MutationCallback = (mutationList, _observer) => {
		for (const mutation of mutationList) {
			if (mutation.type === 'childList') {
				if ([...mutation.addedNodes]
					.some(n => n instanceof HTMLElement && n.classList?.contains('	'))) {
					onLightboxOpen()
				}
				if ([...mutation.removedNodes]
					.some(n => n instanceof HTMLElement && n.classList?.contains('fullscreen-image'))) {
					onLightboxClose()
				}
			} else if (mutation.type === 'attributes' && mutation.target instanceof HTMLElement) {
				// Sometimes we don't catch the removal
				if (mutation.target.matches('.fullscreen-image.fade-leave-active')) {
					onLightboxClose()
				}
			}
		}
	}
	observer = new MutationObserver(callback)
	observer.observe(targetNode, { attributes: true, childList: true, subtree: true })
})

onUnmounted(() => {
	if (observer) {
		observer.disconnect()
		observer = null
	}
})

function onLightboxOpen(): void {
	lightboxOpened.value = true
	console.debug('Lightbox opened')
}

function onLightboxClose(): void {
	console.debug('Lightbox closed')
	if (!lightboxOpened.value) {
		// Don't trigger twice
		return
	}
	lightboxOpened.value = false
	// Refocus to allow keyboard navigation again
	focusKeyboardNav()
}

function previousPageKeyboard():void {
	if (props.keyboardNavigation) {
		previousPage()
	}
}

function nextPageKeyboard(): void {
	if (props.keyboardNavigation) {
		nextPage()
	}
}

function previousPage(): void {
	if (lightboxOpened.value) {
		// lightbox is active, don't do anything
		return
	}
	if (currentPage.value > 1) {
		currentPage.value--
	}
}

function nextPage(): void {
	if (lightboxOpened.value) {
		// lightbox is active, don't do anything
		return
	}
	if (currentPage.value < pageCount.value) {
		currentPage.value++
	}
}
</script>

<style lang="scss">
.c-GalleryIndex {
	// No outline on this artifically focused element
	outline: 0;
}

.c-GalleryIndex__list {
	display: flex;
	flex-wrap: wrap;
	align-items: center;
	justify-content: center;

	img {
		// Crop overly large images
		width: 250px;

		// Note: max-height would put a greater emphasis on the size diversity of derivatives,
		// but would look less like a grid
		height: 400px;
		object-fit: contain;
	}
}

.c-GalleryIndex__image {
	width: 266px;
	padding: 8px;
	margin: 16px;
}

.v-application .c-GalleryIndex__imageLegend {
	font-size: 0.8em;
	text-align: center;
	display: block;

	a {
		color: inherit;

		&:hover {
			color: rgb(var(--v-theme-secondary));
		}
	}
}
</style>
