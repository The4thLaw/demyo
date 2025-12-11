<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Album.identification')" :loading="loading">
				<v-row>
					<v-col cols="12" :md="bookTypeManagement ? 8 : 12">
						<Autocomplete
							v-model="album.series.id" :items="series" :loading="seriesLoading"
							label-key="field.Album.series" clearable refreshable @refresh="loadSeries"
						/>
					</v-col>
					<v-col v-if="bookTypeManagement" cols="12" md="4">
						<Autocomplete
							v-model="album.bookType.id" :items="bookTypes" :loading="bookTypesLoading"
							:rules="rules.bookType" label-key="field.Album.bookType"
							refreshable @refresh="loadBookTypes"
						/>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.cycle" :label="$t('field.Album.cycle')"
							type="number" inputmode="decimal" step="any" :rules="rules.cycle"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.number" :label="$t('field.Album.number')"
							type="number" inputmode="decimal" step="any" :rules="rules.number"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field v-model="album.numberSuffix" :label="$t('field.Album.numberSuffix')" />
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="album.title" :label="$t('field.Album.title')" required
							:rules="rules.title"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field v-model="album.originalTitle" :label="$t('field.Album.originalTitle')" />
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field
							v-if="seriesDetails?.universe?.id"
							v-model="seriesDetails.universe.identifyingName" :label="$t('field.Album.universe')"
							:readonly="true"
						>
							<template #append>
								<v-icon icon="mdi-pencil-off-outline" :title="$t('page.Album.inheritedFromSeries')" />
							</template>
						</v-text-field>
						<Autocomplete
							v-else
							v-model="album.universe.id" :items="universes" :loading="universesLoading"
							:add-component="UniverseLightCreate" add-label="title.add.universe"
							label-key="field.Album.universe" refreshable @refresh="loadUniverses"
							@added="(id: number) => album.universe.id = id"
						/>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="album.genres" :items="filteredGenres" :loading="taxonsLoading"
							multiple clearable
							:add-component="TaxonLightCreate" :add-props="{ type: 'GENRE' }"
							add-label="title.add.taxon.GENRE"
							label-key="field.Taxonomized.genres" refreshable @refresh="loadTaxons"
							@added="(id: number) => album.genres.push(id)"
						/>
						<div v-if="seriesDetails?.genres?.length > 0">
							<div class="v-AlbumEdit__inherited" v-text="$t('page.Album.inheritedFromSeries')" />
							<TaxonLink :model="seriesDetails.genres" />
						</div>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="album.tags" :items="filteredTags" :loading="taxonsLoading"
							multiple clearable
							:add-component="TaxonLightCreate" :add-props="{ type: 'TAG' }"
							add-label="title.add.taxon.TAG"
							label-key="field.Taxonomized.tags" refreshable @refresh="loadTaxons"
							@added="(id: number) => album.tags.push(id)"
						/>
						<div v-if="seriesDetails?.tags?.length > 0">
							<div class="v-AlbumEdit__inherited" v-text="$t('page.Album.inheritedFromSeries')" />
							<TaxonLink :model="seriesDetails.tags" />
						</div>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.publishing')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="album.publisher.id" :items="publishers" :loading="publishersLoading"
							label-key="field.Album.publisher" required :rules="rules.publisher"
							refreshable @refresh="loadPublishers" @change="loadCollections(album)"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="album.collection.id" :items="collections" :loading="collectionsLoading"
							label-key="field.Album.collection" clearable
							refreshable @refresh="loadCollections(album)"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.authoring')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="album.writers" :items="authors" :loading="authorsLoading"
							:label-key="`field.Album.writers.${labelType}`" multiple
							item-title="nameWithPseudonym"
							:add-component="AuthorLightCreate" add-label="title.add.author"
							refreshable @refresh="loadAuthors" @added="(id: number) => album.writers.push(id)"
						/>
					</v-col>
					<v-col v-if="!fieldConfig.has('ALBUM_ARTIST')" cols="12" md="6">
						<Autocomplete
							v-model="album.artists" :items="authors" :loading="authorsLoading"
							:label-key="`field.Album.artists.${labelType}`" multiple
							item-title="nameWithPseudonym"
							:add-component="AuthorLightCreate" add-label="title.add.author"
							refreshable @refresh="loadAuthors" @added="(id: number) => album.artists.push(id)"
						/>
					</v-col>
					<v-col v-if="!fieldConfig.has('ALBUM_COLORIST')" cols="12" md="6">
						<Autocomplete
							v-model="album.colorists" :items="authors" :loading="authorsLoading"
							label-key="field.Album.colorists" multiple
							item-title="nameWithPseudonym"
							:add-component="AuthorLightCreate" add-label="title.add.author"
							refreshable @refresh="loadAuthors" @added="(id: number) => album.colorists.push(id)"
						/>
					</v-col>
					<v-col v-if="!fieldConfig.has('ALBUM_INKER')" cols="12" md="6">
						<Autocomplete
							v-model="album.inkers" :items="authors" :loading="authorsLoading"
							label-key="field.Album.inkers" multiple
							item-title="nameWithPseudonym"
							:add-component="AuthorLightCreate" add-label="title.add.author"
							refreshable @refresh="loadAuthors" @added="(id: number) => album.inkers.push(id)"
						/>
					</v-col>
					<v-col v-if="!fieldConfig.has('ALBUM_TRANSLATOR')" cols="12" md="6">
						<Autocomplete
							v-model="album.translators" :items="authors" :loading="authorsLoading"
							label-key="field.Album.translators" multiple
							item-title="nameWithPseudonym"
							:add-component="AuthorLightCreate" add-label="title.add.author"
							refreshable @refresh="loadAuthors" @added="(id: number) => album.translators.push(id)"
						/>
					</v-col>
					<v-col v-if="!fieldConfig.has('ALBUM_COVER_ARTIST')" cols="12" md="6">
						<Autocomplete
							v-model="album.coverArtists" :items="authors" :loading="authorsLoading"
							label-key="field.Album.coverArtists" multiple
							item-title="nameWithPseudonym"
							:add-component="AuthorLightCreate" add-label="title.add.author"
							refreshable @refresh="loadAuthors" @added="(id: number) => album.coverArtists.push(id)"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('page.Album.aboutEdition')">
				<v-row>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.firstEditionDate" :label="$t('field.Album.firstEditionDate')"
							type="date" @change="adjustEditionDates"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.currentEditionDate" :label="$t('field.Album.currentEditionDate')"
							type="date" :readonly="sameEditionDates"
							:append-icon="sameEditionDates ? 'mdi-pencil-off-outline' : ''"
						/>
						<v-checkbox
							v-model="sameEditionDates" :label="$t('field.Album.currentEditionDate.sameAsFirst')"
							:readonly="album.markedAsFirstEdition" @update:model-value="adjustEditionDates"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-checkbox
							v-model="album.markedAsFirstEdition" :label="$t('field.Album.markedAsFirstEdition.edit')"
							@update:model-value="adjustEditionDates"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.printingDate" :label="$t('field.Album.printingDate')" type="date"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.isbn" :label="$t('field.Album.isbn')" :rules="rules.isbn"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.format')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="album.binding.id" :items="bindings" :loading="bindingsLoading"
							label-key="field.Album.binding" clearable refreshable @refresh="loadBindings"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field v-model="album.location" :label="$t('field.Album.location')" />
						<div v-if="seriesDetails?.location">
							<div class="v-AlbumEdit__inherited" v-text="$t('page.Album.inheritedFromSeries')" />
							{{ seriesDetails?.location }}
						</div>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.width" :label="$t('field.Album.width')"
							type="number" inputmode="decimal" step="any" :rules="rules.width"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.height" :label="$t('field.Album.height')"
							type="number" inputmode="decimal" step="any" :rules="rules.height"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.pages" :label="$t('field.Album.pages')"
							type="number" inputmode="decimal" :rules="rules.pages"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.acquisition')">
				<v-row>
					<v-col cols="12" md="4">
						<v-checkbox v-model="album.wishlist" :label="$t('field.Album.wishlist.edit')" />
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.acquisitionDate" :label="$t('field.Album.acquisitionDate')"
							type="date" :disabled="album.wishlist"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<CurrencyField
							v-model="album.purchasePrice" :disabled="album.wishlist"
							label-key="field.Album.purchasePrice"
						/>
					</v-col>
					<PriceManagement v-model="album" model-name="Album" cols="12" md="6" />
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.freeText')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<span class="dem-fieldlabel">{{ $t('field.Album.summary') }}</span>
						<RichTextEditor v-model="album.summary" />
					</v-col>
					<v-col cols="12" md="6">
						<span class="dem-fieldlabel">{{ $t('field.Album.comment') }}</span>
						<RichTextEditor v-model="album.comment" />
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.images')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="album.cover.id" :items="images" :loading="imagesLoading"
							label-key="field.Album.cover" clearable refreshable @refresh="loadImages"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="album.images" :items="images" :loading="imagesLoading"
							:multiple="true"
							label-key="field.Album.images" refreshable @refresh="loadImages"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<FormActions v-if="!loading" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script setup lang="ts">
import AuthorLightCreate from '@/components/authors/AuthorLightCreate.vue'
import TaxonLightCreate from '@/components/tags/TaxonLightCreate.vue'
import UniverseLightCreate from '@/components/universes/UniverseLightCreate.vue'
import { useSimpleEdit } from '@/composables/model-edit'
import {
	useRefreshableAuthors, useRefreshableBindings, useRefreshableBookTypes, useRefreshableImages,
	useRefreshablePublishers, useRefreshableSeries, useRefreshableTaxons,
	useRefreshableUniverses
} from '@/composables/refreshable-models'
import { getParsedRouteParam } from '@/helpers/route'
import { integer, isbn, mandatory, number, strictlyPositive } from '@/helpers/rules'
import albumService from '@/services/album-service'
import bookTypeService from '@/services/book-type-service'
import publisherService from '@/services/publisher-service'
import seriesService from '@/services/series-service'
import { useRoute } from 'vue-router'

const route = useRoute()

const sameEditionDates = ref(false)

const { bookTypes, bookTypesLoading, loadBookTypes } = useRefreshableBookTypes()
const { authors, authorsLoading, loadAuthors } = useRefreshableAuthors()
const { bindings, bindingsLoading, loadBindings } = useRefreshableBindings()
const { images, imagesLoading, loadImages } = useRefreshableImages()
const { publishers, publishersLoading, loadPublishers } = useRefreshablePublishers()
const { series, seriesLoading, loadSeries } = useRefreshableSeries()
const { genres, tags, taxonsLoading, loadTaxons } = useRefreshableTaxons()
const { universes, universesLoading, loadUniverses } = useRefreshableUniverses()

const collections = ref([] as Collection[])
const collectionsLoading = ref(false)
async function loadCollections(forAlbum: Partial<Album>): Promise<void> {
	collectionsLoading.value = true
	collections.value = await publisherService.findCollectionsForList(forAlbum.publisher?.id)

	if (forAlbum.collection?.id) {
		// If the current album ID is not in the returned list, clear it
		if (!collections.value.find(val => val.id === forAlbum.collection?.id)) {
			forAlbum.collection.id = null as unknown as number
		}
	}
	collectionsLoading.value = false
}

const bookTypeManagement = ref(false)
const seriesDetails: Ref<Series | undefined> = ref(undefined)

async function loadSeriesDetails(newSeriesId?: number): Promise<void> {
	// Clear the current cache
	seriesDetails.value = undefined
	// Check the new applicable one
	if (newSeriesId) {
		seriesDetails.value = await seriesService.findById(newSeriesId)
		// If the new one exists, clear what could be in the album
		if (seriesDetails.value.universe) {
			// eslint-disable-next-line @typescript-eslint/no-use-before-define
			album.value.universe = {} as Universe
		}
	}
}

async function fetchData(id: number | undefined): Promise<Partial<Album>> {
	const btmP = bookTypeService.isManagementEnabled()

	let fetched: Partial<Album>
	if (id) {
		fetched = await albumService.editById(id)
		void loadSeriesDetails(fetched.series?.id)
	} else if (route.query.toSeries) {
		const toSeries = getParsedRouteParam(route.query.toSeries) ?? 0
		void loadSeriesDetails(toSeries)
		fetched = await seriesService.getAlbumTemplate(toSeries)
	} else {
		fetched = {
			bookType: {} as BookType,
			series: {} as Series,
			tags: [],
			writers: [],
			artists: [],
			colorists: [],
			inkers: [],
			translators: [],
			coverArtists: [],
			publisher: {} as Publisher,
			collection: {} as Collection,
			binding: {} as Binding,
			prices: [],
			cover: {} as Image,
			universe: {} as Universe
		}
	}

	// Load collections with the currently available data
	void loadCollections(fetched)

	// Check the edition dates
	sameEditionDates.value = (!!fetched.firstEditionDate
		&& fetched.firstEditionDate === fetched.currentEditionDate) || false

	bookTypeManagement.value = await btmP

	return Promise.resolve(fetched)
}

const { model: album, loading, save, reset } = useSimpleEdit(fetchData, albumService,
	[loadAuthors, loadBindings, loadBookTypes, loadImages, loadPublishers, loadSeries, loadTaxons, loadUniverses],
	'title.add.album', 'title.edit.album', 'AlbumView')

const labelType = computed(() => {
	return bookTypes.value.find(bt => bt.id === album.value.bookType?.id)?.labelType ?? 'GRAPHIC_NOVEL'
})
const fieldConfig = computed(() => {
	return new Set(bookTypes.value.find(bt => bt.id === album.value.bookType?.id)?.structuredFieldConfig ?? [])
})

function adjustEditionDates(): void {
	if (album.value.markedAsFirstEdition) {
		sameEditionDates.value = true
	}
	if (sameEditionDates.value) {
		album.value.currentEditionDate = album.value.firstEditionDate
	}
}

// Try to avoid albums repeating the data from the parent series
const filteredGenres = computed(() =>
	genres.value.filter(g => seriesDetails.value?.genres?.length === 0
		|| !seriesDetails.value?.genres.find(sg => sg.id === g.id)))
const filteredTags = computed(() =>
	tags.value.filter(t => seriesDetails.value?.tags?.length === 0
		|| !seriesDetails.value?.tags.find(st => st.id === t.id)))

watch(() => album.value.series?.id, loadSeriesDetails)

const rules = {
	bookType: [mandatory()],
	cycle: [integer()],
	number: [number()],
	title: [mandatory()],
	publisher: [mandatory()],
	isbn: [isbn()],
	width: [number(), strictlyPositive()],
	height: [number(), strictlyPositive()],
	pages: [integer(), strictlyPositive()]
}
</script>

<style lang="scss">
.v-AlbumEdit__inherited {
	color: rgb(var(--v-theme-primary));
	font-size: 0.8em;
}
</style>
