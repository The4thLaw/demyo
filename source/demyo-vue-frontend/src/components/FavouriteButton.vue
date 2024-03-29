<template>
	<v-btn
		v-if="initialized" icon :loading="loading"
		v-bind="$attrs" @click="toggle"
	>
		<v-icon v-if="isFavourite">
			mdi-heart
		</v-icon>
		<v-icon v-else>
			mdi-heart-outline
		</v-icon>
	</v-btn>
</template>

<script>
import readerService from '@/services/reader-service'
import { useReaderStore } from '@/stores/reader'
import sortedIndexOf from 'lodash/sortedIndexOf'
import { mapState } from 'pinia'

export default {
	name: 'FavouriteButton',

	props: {
		modelId: {
			type: Number,
			required: true
		},

		type: {
			type: String,
			required: true
		}
	},

	data() {
		return {
			initialized: true,
			loading: false
		}
	},

	computed: {
		...mapState(useReaderStore, {
			favourites: function (store) {
				if (this.type === 'Album') {
					return store.favouriteAlbums
				} else if (this.type === 'Series') {
					return store.favouriteSeries
				}
				console.error('Invalid favourite type', this.type)
				return []
			}
		}),

		isFavourite() {
			return sortedIndexOf(this.favourites, this.modelId) > -1
		}
	},

	methods: {
		async toggle() {
			this.loading = true
			// Call right service method depending on type and state
			// Service should alter the store on its own and reactivity should propagate here
			// await !
			if (this.isFavourite) {
				if (this.type === 'Album') {
					await readerService.removeFavouriteAlbum(this.modelId)
				} else if (this.type === 'Series') {
					await readerService.removeFavouriteSeries(this.modelId)
				}
			} else {
				if (this.type === 'Album') {
					await readerService.addFavouriteAlbum(this.modelId)
				} else if (this.type === 'Series') {
					await readerService.addFavouriteSeries(this.modelId)
				}
			}
			this.loading = false
		}
	}
}
</script>
