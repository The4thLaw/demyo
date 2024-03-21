<template>
	<div
		v-if="items"
		ref="keyTarget"
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
				<!-- TODO: Vue 3: equivalent for
					v-img:group="{src: item.baseImageUrl, opened: vimgOpen, closed: vimgClosed}" -->
				<img
					v-if="item.baseImageUrl"
					:src="`${item.baseImageUrl}?w=250`"
					:srcset="`
						${item.baseImageUrl}?w=250 1x,
						${item.baseImageUrl}?w=500 2x`"
					:alt="item.identifyingName"
				>
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
			@update:modelValue="$emit('page-change')"
		/>
	</div>
</template>

<script>
import { focusElement } from '@/helpers/dom'
import { getBaseImageUrl } from '@/helpers/images'
import { useReaderStore } from '@/stores/reader'
// Easier for a dynamic access to the properties
// eslint-disable-next-line you-dont-need-lodash-underscore/get
import get from 'lodash/get'
import { mapState } from 'pinia'

export default {
	name: 'GalleryIndex',

	props: {
		items: {
			type: Array,
			required: true
		},

		imagePath: {
			type: String,
			default: undefined
		},

		bordered: {
			type: Boolean,
			default: false
		},

		keyboardNavigation: {
			type: Boolean,
			default: false
		}
	},

	data() {
		return {
			vimg: false,
			currentPage: 1
		}
	},

	computed: {
		...mapState(useReaderStore, {
			itemsPerPage: store => store.currentReader.configuration.pageSizeForImages
		}),

		hasDefaultSlot() {
			return !!this.$slots.default
		},

		paginatedItems() {
			const slice = this.items.slice((this.currentPage - 1) * this.itemsPerPage,
				this.currentPage * this.itemsPerPage)
			slice.map(item => {
				let image
				if (this.imagePath) {
					image = get(item, this.imagePath)
				} else {
					image = item
				}
				if (image) { // Some entries may not have an image at all
					item.baseImageUrl = getBaseImageUrl(image)
				}
			})
			return slice
		},

		pageCount() {
			return Math.ceil(this.items.length / this.itemsPerPage)
		}
	},

	mounted() {
		if (this.keyboardNavigation) {
			// Focus may cause the browser to scroll to the element. It's fine if the element is the main one on the
			// page but causes issues once it's further down, like in the AlbumView
			focusElement(this.$refs.keyTarget)
		}
	},

	methods: {
		vimgOpen() {
			this.vimg = true
		},

		vimgClosed() {
			this.vimg = false
			// Refocus to allow keyboard navigation again
			focusElement(this.$refs.keyTarget)
		},

		previousPageKeyboard() {
			if (this.keyboardNavigation) {
				this.previousPage()
			}
		},

		nextPageKeyboard() {
			if (this.keyboardNavigation) {
				this.nextPage()
			}
		},

		previousPage() {
			if (this.vimg) {
				// v-img is active, don't do anything
				return
			}
			if (this.currentPage > 1) {
				this.currentPage--
			}
		},

		nextPage() {
			if (this.vimg) {
				// v-img is active, don't do anything
				return
			}
			console.log('do')
			if (this.currentPage < this.pageCount) {
				this.currentPage++
			}
		}
	}
}
</script>

<style lang="less">
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
