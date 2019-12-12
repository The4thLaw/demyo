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
		<div v-if="!splitByFirstLetter">
			<div v-for="item in paginatedItems" :key="item.id">
				image {{item.id}}
				<!-- TODO: v-img doesn't seem to form a gallery -->
				<!-- TODO: the page handlers steal the next/previous image events -->
				<!-- TODO: image sizes aren't right -->
				<img
					v-img="{src: `${item.baseImageUrl}`}"
					:src="`${item.baseImageUrl}?w=200`"
					:srcset="`
						${item.baseImageUrl}?w=200 1x,
						${item.baseImageUrl}?w=400 2x`"
				>
				<slot :item="item" />
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
import { get, map } from 'lodash'
import { focusElement } from '@/helpers/dom'

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
				let encodedName = encodeURI(image.userFileName)
				item.baseImageUrl = '/images/' + image.id + '/file/' + encodedName
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
		previousPage() {
			if (this.currentPage > 1) {
				this.currentPage--
			}
		},

		nextPage() {
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
</style>
