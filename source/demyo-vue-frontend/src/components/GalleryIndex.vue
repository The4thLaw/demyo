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
			<div v-for="item in paginatedItems" :key="item.id" class="c-GalleryIndex__image">
				<v-sheet class="pa-2">
					<img
						v-img:group="{src: `${item.baseImageUrl}`}"
						:src="`${item.baseImageUrl}?w=200`"
						:srcset="`
							${item.baseImageUrl}?w=200 1x,
							${item.baseImageUrl}?w=400 2x`"
					>
					<legend v-if="hasDefaultSlot" class="c-GalleryIndex__imageLegend">
						<slot :item="item" />
					</legend>
				</v-sheet>
			</div>
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
			// TODO: load this from the config
			itemsPerPage: 20,
			currentPage: 1
		}
	},

	computed: {
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
				item.baseImageUrl = '/images/' + image.id + '/file/' + getEncodedImageName(image)
			})
			return slice
		},

		pageCount() {
			return Math.ceil(this.items.length / this.itemsPerPage)
		}
	},

	mounted() {
		// TODO: check if there is an event that would allow us to detect v-img being opened/closed
		// so that we can better prevent intercepting key presses and restore the focus
		focusElement(this.$refs.keyTarget)
	},

	methods: {
		previousPage() {
			if (document.querySelector('.fullscreen-v-img')) {
				// v-img is active, don't do anything
				return
			}
			if (this.currentPage > 1) {
				this.currentPage--
			}
		},

		nextPage() {
			if (document.querySelector('.fullscreen-v-img')) {
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
	display: grid;
	grid-template-columns: repeat(auto-fit, 216px);
	grid-gap: 32px;
	justify-items: center;
	align-items: center;
	justify-content: center;

	img {
		// Crop overly large images
		width: 200px;
		max-height: 350px;
		object-fit: none;
	}
}

.c-GalleryIndex__image {
	text-align: center;
}

.v-application .c-GalleryIndex__imageLegend {
	font-size: 0.8em;

	a {
		color: inherit;

		&:hover {
			color: var(--v-anchor-base);
		}
	}
}
</style>
