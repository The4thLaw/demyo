<template>
	<div
		ref="keyTarget"
		v-touch="{
			left: nextPage,
			right: previousPage
		}"
		class="c-GalleryIndex"
		@keyup.arrow-left.exact="previousPage()"
		@keyup.arrow-right.exact="nextPage()"
	>
		<div class="c-GalleryIndex__list">
			<v-sheet v-for="item in paginatedItems" :key="item.id" class="c-GalleryIndex__image">
				<img
					v-if="item.baseImageUrl"
					v-img:group="{src: item.baseImageUrl, opened: vimgOpen, closed: vimgClosed}"
					:src="`${item.baseImageUrl}?w=250`"
					:srcset="`
						${item.baseImageUrl}?w=250 1x,
						${item.baseImageUrl}?w=500 2x`"
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
		/>
	</div>
</template>

<script>
import { get } from 'lodash'
import { mapState } from 'vuex'
import { focusElement } from '@/helpers/dom'
import { getEncodedImageName } from '@/helpers/images'

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
		}
	},

	data() {
		return {
			vimg: false,
			currentPage: 1
		}
	},

	computed: {
		...mapState({
			itemsPerPage: state => state.reader.currentReader.configuration.pageSizeForImages || 20
		}),

		hasDefaultSlot() {
			return !!this.$scopedSlots.default
		},

		paginatedItems() {
			let slice = this.items.slice((this.currentPage - 1) * this.itemsPerPage,
				this.currentPage * this.itemsPerPage)
			slice.map(item => {
				let image
				if (this.imagePath) {
					image = get(item, this.imagePath)
				} else {
					image = item
				}
				if (image) { // Some entries may not have an image at all
					item.baseImageUrl = '/images/' + image.id + '/file/' + getEncodedImageName(image)
				}
			})
			return slice
		},

		pageCount() {
			return Math.ceil(this.items.length / this.itemsPerPage)
		}
	},

	mounted() {
		focusElement(this.$refs.keyTarget)
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
			color: var(--v-anchor-base);
		}
	}
}
</style>
