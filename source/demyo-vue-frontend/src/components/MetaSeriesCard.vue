<template>
	<v-card :flat="true" :hover="true" class="c-MetaSeriesCard">
		<template v-if="meta.album">
			<router-link
				v-ripple :to="cardLink"
				class="c-MetaSeriesCard__title" role="heading" arial-level="3"
			>
				{{ meta.album.title }}
			</router-link>
			<v-list dense>
				<v-list-item :to="cardLink" class="c-MetaSeriesCard__one-shot">
					<v-list-item-content>
						<v-list-item-title>
							{{ $t('field.Album.oneShot.view') }}
						</v-list-item-title>
					</v-list-item-content>
				</v-list-item>
			</v-list>
		</template>
		<template v-if="meta.series">
			<router-link
				v-ripple :to="cardLink"
				class="c-MetaSeriesCard__title" role="heading" arial-level="3"
			>
				{{ meta.series.identifyingName }}
			</router-link>
			<v-list dense>
				<v-list-item v-for="album in paginatedItems" :key="album.id" :to="`/albums/${album.id}/view`">
					<v-list-item-content>
						<v-list-item-title :title="album.title">
							{{ album.title }}
						</v-list-item-title>
					</v-list-item-content>
					<v-list-item-action v-if="album.wishlist">
						<v-icon color="grey lighten-1" small>
							mdi-gift
						</v-icon>
					</v-list-item-action>
				</v-list-item>
				<!--
					Pad the last page to keep a constant height.
					If we don't pad and the grid row doesn't have an element that is tall enough, the web page will jump
					when we reach the last item page (e.g. it's suddenly 1 item long and needs a lot less space).
				-->
				<template v-if="pageCount > 1 && !hasNextPage">
					<v-list-item v-for="pad in (itemsPerPage - paginatedItems.length)" :key="pad" />
				</template>
				<ItemCardPagination
					:page-count="pageCount" :has-previous-page="hasPreviousPage"
					:has-next-page="hasNextPage" @prev-page="previousPage" @next-page="nextPage"
				/>
			</v-list>
		</template>
	</v-card>
</template>

<script>
import ItemCardPagination from '@/components/ItemCardPagination'
import paginatedTextMixin from '@/mixins/paginated-text'
import { useReaderStore } from '@/stores/reader'
import { mapState } from 'pinia'

export default {
	name: 'MetaSeriesCard',

	components: {
		ItemCardPagination
	},

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

		...mapState(useReaderStore, {
			itemsPerPage: store => store.currentReader.configuration.subItemsInCardIndex
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
@import "../styles/detached-rulesets.less";

.v-application .c-MetaSeriesCard .v-list-item--link:hover {
	text-decoration: none;
}

// Override default style since the card cannot be clicked
.c-MetaSeriesCard.v-card--hover {
	@dem-dr-model-card--hover();
}

.v-application a.c-MetaSeriesCard__title {
	@dem-dr-model-card-title();
}

.c-MetaSeriesCard__one-shot {
	font-style: italic;
}

// Need an ID override for this particular case
#demyo .c-MetaSeriesCard__one-shot {
	color: var(--dem-text-lighter) !important;
}
</style>
