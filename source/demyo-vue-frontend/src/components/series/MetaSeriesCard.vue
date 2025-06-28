<template>
	<v-card :flat="true" :hover="true" class="c-MetaSeriesCard">
		<template v-if="meta.album">
			<router-link
				v-ripple :to="cardLink"
				class="c-MetaSeriesCard__title" role="heading" aria-level="3"
			>
				{{ meta.album.title }}
			</router-link>
			<v-list density="compact">
				<v-list-item
					:to="cardLink" class="c-MetaSeriesCard__one-shot" :title="$t('field.Album.oneShot.view')"
				/>
			</v-list>
		</template>
		<template v-if="meta.series">
			<router-link
				v-ripple :to="cardLink"
				class="c-MetaSeriesCard__title" role="heading" aria-level="3"
			>
				{{ meta.series.identifyingName }}
			</router-link>
			<v-list density="compact">
				<!-- eslint complains about a duplicate attribute but it's correct according to the Vuetify docs -->
				<!-- eslint-disable vue/no-duplicate-attributes -->
				<v-list-item
					v-for="album in paginatedItems" :key="album.id"
					:to="`/albums/${album.id}/view`"
					:title="album.title" :title.attr="album.title"
				>
					<template v-if="album.wishlist" #append>
						<v-icon size="small" :title="$t('field.Album.wishlist.value.true')">
							mdi-gift
						</v-icon>
					</template>
				</v-list-item>
				<!-- eslint-enable vue/no-duplicate-attributes -->
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

<script setup lang="ts">
import { useBasicPagination } from '@/composables/pagination'
import { useReaderStore } from '@/stores/reader'

const props = defineProps<{
	meta: MetaSeries
}>()
const albums = computed(() => props.meta.albums || [])
const cardLink = computed(() =>
	props.meta.series ? `/series/${props.meta.series.id}/view` : `/albums/${props.meta.album.id}/view`)

const readerStore = useReaderStore()
const itemsPerPage = computed(() => readerStore.currentReader.configuration.subItemsInCardIndex)

const { pageCount, paginatedItems, previousPage, nextPage, hasPreviousPage, hasNextPage }
	= useBasicPagination(albums, itemsPerPage)
</script>

<style lang="scss">
@use '@/styles/mixins';

.v-application .c-MetaSeriesCard .v-list-item--link:hover {
	text-decoration: none;
}

.v-application a.c-MetaSeriesCard__title {
	@include mixins.dem-model-card-title;
}

.c-MetaSeriesCard__one-shot {
	font-style: italic;
}

// Need an ID override for this particular case
#demyo .c-MetaSeriesCard__one-shot {
	color: var(--dem-text-lighter) !important;
}
</style>
