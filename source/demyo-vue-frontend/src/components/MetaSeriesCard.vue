<template>
	<v-card :flat="true" :hover="true" class="c-MetaSeriesCard">
		<!-- TODO: fix layout when only two series on a wide page -->
		<template v-if="meta.album">
			<router-link
				v-ripple :to="cardLink"
				class="c-MetaSeriesCard__title" role="heading" arial-level="3"
			>
				{{ meta.album.title }}
			</router-link>
			<div class="c-MetaSeriesCard__one-shot">
				{{ $t('field.Album.oneShot.view') }}
			</div>
		</template>
		<template v-if="meta.series">
			<router-link
				v-ripple :to="cardLink"
				class="c-MetaSeriesCard__title" role="heading" arial-level="3"
			>
				{{ meta.series.identifyingName }}
			</router-link>
			<v-list dense>
				<v-list-item v-for="album in paginatedItems" :key="album.id" :href="`/albums/${album.id}/view`">
					<v-list-item-content>
						<v-list-item-title :title="album.title">
							{{ album.title }}
						</v-list-item-title>
					</v-list-item-content>
				</v-list-item>
				<v-list-item v-if="pageCount > 1">
					<!-- Custom pagination because vuetify doesn't provide no-count pagination -->
					<v-btn text icon @click.stop="previousPage">
						<v-icon>mdi-chevron-left</v-icon>
					</v-btn>
					<!-- TODO: disable links when not working -->
					<!-- TODO: try to keep a fixed height to the element (pad with empty lines ?) -->
					<v-btn text icon @click.stop="nextPage">
						<v-icon>mdi-chevron-right</v-icon>
					</v-btn>
				</v-list-item>
			</v-list>
		</template>
	</v-card>
</template>

<script>
import { mapState } from 'vuex'
import paginatedTextMixin from '@/mixins/paginated-text'

export default {
	name: 'MetaSeriesCard',

	mixins: [paginatedTextMixin],

	props: {
		meta: {
			type: null,
			required: true
		}
	},

	computed: {
		// Having these allows us to reuse the logic from paginated-text
		itemsToPaginate() {
			return this.meta.albums || []
		},

		...mapState({
			itemsPerPage: state => state.reader.currentReader.configuration.albumsInSeriesIndex || 5
		}),

		cardLink() {
			return this.meta.series
				? `/series/${this.meta.series.id}/view` : `/albums/${this.meta.album.id}/view`
		}
	},

	methods: {
		// Having this allows us to reuse the logic from paginated-text
		firstLetterExtractor: () => '#'
	}
}
</script>

<style lang="less">
// Override default style since the card cannot be clicked
.c-MetaSeriesCard.v-card--hover {
	cursor: default;
}

.v-application a.c-MetaSeriesCard__title {
	font-size: 18px;
	font-weight: 600;
	display: block;
	color: inherit;
	text-decoration: none;
	cursor: pointer;
	padding: 8px 16px;

	&:hover,
	&:focus {
		background: rgba(0, 0, 0, 0.04);
		text-decoration: none;
	}
}

// TODO: better styling
.c-MetaSeriesCard__one-shot {
	font-style: italic;
}
</style>
