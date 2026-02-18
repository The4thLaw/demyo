<template>
	<v-container fluid>
		<Teleport v-if="!loading" to="#teleport-appBarAddons">
			<FavouriteButton :model-id="series.id" type="Series" />
		</Teleport>

		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.series')"
				:to="`/series/${series.id}/edit`"
				icon="mdi-animation dem-overlay-edit"
			/>
			<AppTask
				v-if="albumsLoaded && albumCount <= 0 && derivativeCount <= 0"
				:label="$t('quickTasks.delete.series')"
				:confirm="$t('quickTasks.delete.series.confirm')"
				icon="mdi-animation dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteModel"
			/>
			<AppTask
				v-if="hasAlbumsOutsideReadingList"
				:label="$t('quickTasks.add.series.to.readingList')"
				:confirm="$t('quickTasks.add.series.to.readingList.confirm')"
				icon="mdi-library"
				@confirm="addSeriesToReadingList"
			/>
			<AppTask
				:label="$t('quickTasks.add.album.to.series')"
				:to="{ name: 'AlbumAdd', query: { toSeries: series.id } }"
				icon="mdi-book-open-variant dem-overlay-add"
			/>
			<AppTask
				:label="$t('quickTasks.add.derivative.to.series')"
				:to="{ name: 'DerivativeAdd', query: derivativeQuery }"
				icon="mdi-image-frame dem-overlay-add"
			/>
		</AppTasks>

		<SectionCard :loading="loading" :title="series.identifyingName">
			<div v-if="series.originalName" class="c-SeriesView__originalName">
				({{ series.originalName }})
			</div>

			<FieldValue v-if="series.universe.id" :label="$t('field.Series.universe')">
				<ModelLink :model="series.universe" view="UniverseView" />
			</FieldValue>

			<FieldValue v-if="series.website" :label="$t('field.Series.website')">
				<a :href="series.website">{{ series.website }}</a>
			</FieldValue>

			<FieldValue :label="$t('field.Series.completed.view')">
				{{ completionLabel }}
			</FieldValue>

			<FieldValue v-if="series.location" :label="$t('field.Series.location')">
				{{ series.location }}
			</FieldValue>

			<FieldValue v-if="allGenres.length" :label="$t('field.Taxonomized.genres', allGenres.length)">
				<TaxonLink :model="allGenres" />
			</FieldValue>
			<FieldValue v-if="allTags.length" :label="$t('field.Taxonomized.tags', allTags.length)">
				<TaxonLink :model="allTags" />
			</FieldValue>

			<div class="dem-columnized">
				<FieldValue :value="series.summary" label-key="field.Series.summary" type="rich-text" />
				<FieldValue :value="series.comment" label-key="field.Series.comment" type="rich-text" />
			</div>
		</SectionCard>

		<SectionCard
			v-if="loading || series.albumIds || derivativeCount > 0"
			ref="content-section" class="c-SectionCard--tabbed"
		>
			<v-tabs v-model="currentTab" bg-color="primary" grow>
				<v-tab value="albums" :disabled="albumsLoaded && albumCount <= 0">
					<v-icon start>
						mdi-book-open-variant
					</v-icon>
					{{ $t('field.Series.albums') }}
				</v-tab>
				<v-tab value="derivatives" :disabled="derivativeCount <= 0" @group:selected="loadDerivatives">
					<v-icon start>
						mdi-image-frame
					</v-icon>
					{{ $t('field.Series.derivatives', derivativeCount) }}
				</v-tab>
			</v-tabs>

			<!-- Albums -->
			<v-window v-model="currentTab">
				<v-window-item value="albums" class="dem-tab">
					<div v-if="albumsLoaded" class="c-SeriesView__albumAggregateData dem-columnized pb-4">
						<FieldValue :label="$t('field.Series.albumCount')">
							<template v-if="albumCount === ownedAlbumCount">
								{{ $t('field.Series.albumCount.count.full', [albumCount]) }}
							</template>
							<template v-else>
								{{ $t('field.Series.albumCount.count.partial', [ownedAlbumCount, albumCount]) }}
							</template>
						</FieldValue>

						<FieldValue
							v-if="allPublishers.length" :label="$t('field.Album.publisher', allPublishers.length)"
						>
							<ModelLink :model="allPublishers" view="PublisherView" />
						</FieldValue>

						<FieldValue
							:value="authorOrigins" label-key="field.Series.origin" type="text"
						/>

						<FieldValue v-if="allWriters.length" :label="allWritersLabels">
							<ModelLink :model="allWriters" view="AuthorPseudonym" />
						</FieldValue>
						<FieldValue v-if="allArtists.length" :label="allArtistsLabels">
							<ModelLink :model="allArtists" view="AuthorPseudonym" />
						</FieldValue>
						<FieldValue
							v-if="allColorists.length" :label="$t('field.Album.colorists', allColorists.length)"
						>
							<ModelLink :model="allColorists" view="AuthorPseudonym" />
						</FieldValue>
						<FieldValue v-if="allInkers.length" :label="$t('field.Album.inkers', allInkers.length)">
							<ModelLink :model="allInkers" view="AuthorPseudonym" />
						</FieldValue>
						<FieldValue
							v-if="allTranslators.length" :label="$t('field.Album.translators', allTranslators.length)"
						>
							<ModelLink :model="allTranslators" view="AuthorPseudonym" />
						</FieldValue>
						<FieldValue
							v-if="allCoverArtists.length"
							:label="$t('field.Album.coverArtists', allCoverArtists.length)"
						>
							<ModelLink :model="allCoverArtists" view="AuthorPseudonym" />
						</FieldValue>
					</div>
					<v-switch
						v-if="albumCount !== ownedAlbumCount" v-model="showWishlist"
						:label="$t('page.Series.showWishlist')" prepend-icon="mdi-gift"
					/>
					<v-row>
						<v-col
							v-for="albumId in filteredIds" :key="albumId"
							cols="12" md="6" lg="4"
						>
							<AlbumCard
								v-if="albums[albumId]"
								:album="albums[albumId]" :loading="albums[albumId].loading"
								:load-cover="albumsLoaded"
							/>
						</v-col>
					</v-row>
				</v-window-item>

				<!-- Derivatives -->
				<v-window-item value="derivatives" class="dem-tab">
					<div v-if="derivatives.length <= 0" class="text-center">
						<v-progress-circular indeterminate color="primary" size="64" />
					</div>
					<GalleryIndex
						:items="derivatives" image-path="mainImage" bordered
						:keyboard-navigation="false"
						@page-change="contentSectionRef.$el.scrollIntoView()"
					>
						<template #default="slotProps">
							<router-link :to="`/derivatives/${slotProps.item.id}/view`">
								<div v-if="slotProps.item.album">
									{{ slotProps.item.album.title }}
								</div>
								<div v-if="slotProps.item.source">
									{{ slotProps.item.source.identifyingName }}
								</div>
							</router-link>
						</template>
					</GalleryIndex>
				</v-window-item>
			</v-window>
		</SectionCard>
	</v-container>
</template>

<script setup lang="ts">
import SectionCard from '@/components/generic/SectionCard.vue'
import { useSimpleView } from '@/composables/model-view'
import { useAuthorCountries } from '@/helpers/countries'
import { mergeModels } from '@/helpers/fields'
import albumService from '@/services/album-service'
import derivativeService from '@/services/derivative-service'
import readerService from '@/services/reader-service'
import seriesService from '@/services/series-service'
import { useReaderStore } from '@/stores/reader'
import sortedIndexOf from 'lodash/sortedIndexOf'
import asyncPool from 'tiny-async-pool'
import { useTemplateRef } from 'vue'
import { useI18n } from 'vue-i18n'

interface LoadableAlbum extends Partial<Album> {
	loading?: boolean
}

const i18n = useI18n()
const readerStore = useReaderStore()

const albums = ref({} as Record<number, LoadableAlbum>)
const albumsLoaded = ref(false)
const derivatives = ref([] as Derivative[])
const derivativeCount = ref(-1)
const showWishlist = ref(true)
const currentTab = ref('albums')

async function fetchData(id: number): Promise<Series> {
	// Clear some data. Else it's kept from one series to the next when navigating
	albums.value = {}
	currentTab.value = 'albums'
	derivatives.value = []

	const dcPromise = seriesService.countDerivatives(id)

	const fetched = await seriesService.findById(id)

	if (fetched.albumIds) {
		fetched.albumIds.forEach(aid => {
			albums.value[aid] = {
				loading: true
			}
		})
	}

	void loadAlbums(fetched)

	derivativeCount.value = await dcPromise

	return Promise.resolve(fetched)
}

const { model: series, appTasksMenu, loading, deleteModel } = useSimpleView(
	fetchData, seriesService, 'quickTasks.delete.series.confirm.done', 'SeriesIndex')

const ownedIds = computed(() => {
	if (!series.value.albumIds) {
		return []
	}
	return series.value.albumIds.filter(id => {
		const album = albums.value[id]
		if (!album) {
			return false
		}
		return !!album.loading || !album.wishlist
	})
})

const filteredIds = computed(() => {
	if (showWishlist.value) {
		return series.value.albumIds
	}
	return ownedIds.value
})

const albumsArray = computed(() => albumsLoaded.value ? Object.values(albums.value) as Album[] : [])
const albumCount = computed(() => series.value.albumIds ? series.value.albumIds.length : 0)
const ownedAlbumCount = computed(() => ownedIds.value.length)
const allPublishers = computed(() =>
	albumsLoaded.value ? mergeModels(albumsArray.value, 'publisher', 'identifyingName') : [])
const allBookTypes = computed(() =>
	albumsLoaded.value ? mergeModels<Album, BookType>(albumsArray.value, 'bookType', ['identifyingName']) : [])
const allWriters = computed(() =>
	albumsLoaded.value ? mergeModels<Album, Author>(albumsArray.value, 'writers', ['name', 'firstName']) : [])
const allArtists = computed(() =>
	albumsLoaded.value ? mergeModels<Album, Author>(albumsArray.value, 'artists', ['name', 'firstName']) : [])
const allColorists = computed(() =>
	albumsLoaded.value ? mergeModels<Album, Author>(albumsArray.value, 'colorists', ['name', 'firstName']) : [])
const allInkers = computed(() =>
	albumsLoaded.value ? mergeModels<Album, Author>(albumsArray.value, 'inkers', ['name', 'firstName']) : [])
const allTranslators = computed(() =>
	albumsLoaded.value ? mergeModels<Album, Author>(albumsArray.value, 'translators', ['name', 'firstName']) : [])
const allCoverArtists = computed(() =>
	albumsLoaded.value ? mergeModels<Album, Author>(albumsArray.value, 'coverArtists', ['name', 'firstName']) : [])
const allOriginAuthors = computed(() => [
	...allWriters.value,
	...allArtists.value,
	...allColorists.value,
	...allInkers.value,
	...allCoverArtists.value
])
const authorOrigins = useAuthorCountries(allOriginAuthors)
const allGenres = computed(() => albumsLoaded.value
	? mergeModels([series.value, ...albumsArray.value], 'genres', 'identifyingName')
	: series.value.genres)
const allTags = computed(() => albumsLoaded.value
	? mergeModels([series.value, ...albumsArray.value], 'tags', 'identifyingName')
	: series.value.tags)

const allWritersLabels = computed(() => {
	return allBookTypes.value
		.map(bt => `field.Album.writers.${bt.labelType}`)
		.map(l => i18n.t(l, allWriters.value.length))
		.sort((l1, l2) => l1.localeCompare(l2, i18n.locale.value))
		.join(', ')
})

const allArtistsLabels = computed(() => {
	return allBookTypes.value
		.map(bt => `field.Album.artists.${bt.labelType}`)
		.map(l => i18n.t(l, allArtists.value.length))
		.sort((l1, l2) => l1.localeCompare(l2, i18n.locale.value))
		.join(', ')
})

const authorsAlive = computed(() => {
	const relevantAuthors = [...allWriters.value, ...allArtists.value]
	return relevantAuthors.length === 0 || relevantAuthors.some(a => !a.deathDate)
})

const albumDates = computed(() => {
	if (!albumsLoaded.value) {
		return []
	}

	return albumsArray.value
		.map(a => a.firstEditionDate || a.currentEditionDate || a.printingDate)
		.filter(Boolean)
		.map(d => new Date(d))
})

const minAlbumYear = computed(() => {
	if (!albumDates.value.length) {
		return null
	}

	const minDate = new Date(Math.min.apply(null, albumDates.value.map(d => d.getTime())))
	return minDate.getFullYear()
})

const maxAlbumYear = computed(() => {
	if (!albumDates.value.length) {
		return null
	}
	if (!series.value.completed) {
		return null
	}

	const maxDate = new Date(Math.max.apply(null, albumDates.value.map(d => d.getTime())))
	return maxDate.getFullYear()
})

const completionLabel = computed(() => {
	if (series.value.completed) {
		if (!minAlbumYear.value || !maxAlbumYear.value) {
			return i18n.t('field.Series.completed.value.trueNoDates')
		}
		return i18n.t('field.Series.completed.value.true', { min: minAlbumYear.value, max: maxAlbumYear.value })
	}

	if (authorsAlive.value) {
		if (!minAlbumYear.value) {
			return i18n.t('field.Series.completed.value.falseNoDates')
		}
		return i18n.t('field.Series.completed.value.false', { min: minAlbumYear.value })
	}

	if (!minAlbumYear.value) {
		return i18n.t('page.Series.allAuthorsDeceased.withoutDate')
	}
	return i18n.t('page.Series.allAuthorsDeceased.withDate', { min: minAlbumYear.value })
})

const derivativeQuery = computed(() => {
	const query = {
		toSeries: series.value.id,
		toArtist: null as number | null
	}
	if (allArtists.value.length === 1) {
		query.toArtist = allArtists.value[0].id
	}
	// If not all albums are loaded, let impatient users add the Derivative just to the Series
	return query
})

const hasAlbumsOutsideReadingList = computed(() => {
	if (!albumsLoaded.value) {
		return false
	}

	return albumsArray.value.some(a => sortedIndexOf(readerStore.readingList, a.id) <= -1)
})

async function loadAlbums(forSeries: Series): Promise<void> {
	if (forSeries.albumIds) {
		// We need the variable to iterate and, without iteration, this doesn't work

		for await (const _value of asyncPool(2, forSeries.albumIds, albumLoader)) {
			// Nothing to do
		}
	}
	albumsLoaded.value = true

	if (albumCount.value === 0 && derivativeCount.value > 0) {
		// If there are no albums but there are derivatives, load the derivatives
		currentTab.value = 'derivatives'
		void loadDerivatives()
	}
}

async function albumLoader(id: number): Promise<void> {
	const album = await albumService.findById(id)
	albums.value[id] = album
	albums.value[id].loading = false
}

async function addSeriesToReadingList(): Promise<void> {
	appTasksMenu.value = false
	return readerService.addSeriesToReadingList(series.value.id)
}

async function loadDerivatives(): Promise<void> {
	if (derivatives.value.length > 0) {
		// Don't reload every time
		return
	}
	if (loading.value) {
		// Don't reload while the page is loading, the series might not be initialized yet
		return
	}
	derivatives.value = await derivativeService.findForIndex({ series: series.value.id })
}

const contentSectionRef = useTemplateRef<typeof SectionCard>('content-section')
</script>

<style lang="scss">
.c-SeriesView__originalName {
	margin-top: -1em;
	opacity: 0.87;
}

.c-SeriesView__albumAggregateData {
	// Must rely on rgba to use Vuetify variables
	/* stylelint-disable-next-line color-function-notation */
	border-bottom: 1px solid rgb(var(--v-border-color), var(--v-border-opacity));
	margin-bottom: 1.5em;
}
</style>
