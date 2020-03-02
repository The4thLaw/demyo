<template>
	<v-btn v-if="initialized" icon :loading="loading" v-bind="$attrs" @click="toggle">
		<v-icon v-if="isFavourite">mdi-heart</v-icon>
		<v-icon v-else>mdi-heart-outline</v-icon>
	</v-btn>
</template>

<script>
import { sortedIndexOf } from 'lodash'
import { mapState } from 'vuex'
// TODO: handle click ! :)

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
		...mapState({
			favourites: function (state) {
				if (this.type === 'Album') {
					return state.reader.favouriteAlbums
				} else if (this.type === 'Series') {
					return state.reader.favouriteSeries
				}
				console.error('Invalid favourite type', this.type)
				return new Set()
			}
		}),

		isFavourite() {
			return sortedIndexOf(this.favourites, this.modelId) > -1
		}
	},

	methods: {
		toggle() {
			// Call right service method depending on type and state
			// Service should alter the store on its own
		}
	}
}
</script>
