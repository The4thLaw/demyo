<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Album.identification')" :loading="loading">
				<v-row>
					<v-col cols="12">
						<AutoComplete
							v-model="album.series.id" :items="series" :loading="seriesLoading"
							label-key="field.Album.series" clearable refreshable @refresh="loadSeries"
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
					<v-col cols="12">
						<AutoComplete
							v-model="album.tags" :items="tags" :loading="tagsLoading"
							multiple clearable
							label-key="field.Album.tags" refreshable @refresh="loadTags"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.publishing')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.publisher.id" :items="publishers" :loading="publishersLoading"
							label-key="field.Album.publisher" required :rules="rules.publisher"
							refreshable @refresh="loadPublishers" @input="loadCollections(album)"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
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
						<AutoComplete
							v-model="album.writers" :items="authors" :loading="authorsLoading"
							label-key="field.Album.writers" multiple refreshable @refresh="loadAuthors"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.artists" :items="authors" :loading="authorsLoading"
							label-key="field.Album.artists" multiple refreshable @refresh="loadAuthors"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.colorists" :items="authors" :loading="authorsLoading"
							label-key="field.Album.colorists" multiple refreshable @refresh="loadAuthors"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.inkers" :items="authors" :loading="authorsLoading"
							label-key="field.Album.inkers" multiple refreshable @refresh="loadAuthors"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.translators" :items="authors" :loading="authorsLoading"
							label-key="field.Album.translators" multiple refreshable @refresh="loadAuthors"
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
							:readonly="album.markedAsFirstEdition" @update:modelValue="adjustEditionDates"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-checkbox
							v-model="album.markedAsFirstEdition" :label="$t('field.Album.markedAsFirstEdition.edit')"
							@update:modelValue="adjustEditionDates"
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
						<AutoComplete
							v-model="album.binding.id" :items="bindings" :loading="bindingsLoading"
							label-key="field.Album.binding" clearable refreshable @refresh="loadBindings"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field v-model="album.location" :label="$t('field.Album.location')" />
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.width" :label="$t('field.Album.width')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.height" :label="$t('field.Album.height')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.pages" :label="$t('field.Album.pages')"
							type="number" inputmode="decimal"
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
						<label class="dem-fieldlabel">{{ $t('field.Album.summary') }}</label>
						<RichTextEditor v-model="album.summary" />
					</v-col>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">{{ $t('field.Album.comment') }}</label>
						<RichTextEditor v-model="album.comment" />
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.images')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.cover.id" :items="images" :loading="imagesLoading"
							label-key="field.Album.cover" refreshable @refresh="loadImages"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
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
import { useSimpleEdit } from '@/composables/model-edit'
import { useRefreshableAuthors, useRefreshableBindings, useRefreshableImages, useRefreshablePublishers, useRefreshableSeries, useRefreshableTags } from '@/composables/refreshable-models'
import { getParsedRouteParam } from '@/helpers/route'
import { integer, isbn, mandatory, number } from '@/helpers/rules'
import albumService from '@/services/album-service'
import publisherService from '@/services/publisher-service'
import seriesService from '@/services/series-service'
import { useRoute } from 'vue-router'

const route = useRoute()

const sameEditionDates = ref(false)

const {authors, authorsLoading, loadAuthors} = useRefreshableAuthors()
const {bindings, bindingsLoading, loadBindings} = useRefreshableBindings()
const {images, imagesLoading, loadImages} = useRefreshableImages()
const {publishers, publishersLoading, loadPublishers} = useRefreshablePublishers()
const {series, seriesLoading, loadSeries} = useRefreshableSeries()
const {tags, tagsLoading, loadTags} = useRefreshableTags()

const collections = ref([] as Collection[])
const collectionsLoading = ref(false)
async function loadCollections(album: Partial<Album>) {
	collectionsLoading.value = true
	collections.value = await publisherService.findCollectionsForList(album.publisher?.id)

	if (album.collection?.id) {
		// If the current album ID is not in the returned list, clear it
		if (!collections.value.find(val => val.id === album.collection?.id)) {
			album.collection.id = null as unknown as number
		}
	}
	collectionsLoading.value = false
}

async function fetchData(id: number|undefined): Promise<Partial<Album>> {
	let album: Partial<Album>
	if (id) {
		album = await albumService.findById(id)
	} else if (route.query.toSeries) {
		album = await seriesService.getAlbumTemplate(getParsedRouteParam(route.query.toSeries) ?? 0)
	} else {
		album = {
			series: {} as Series,
			writers: [],
			artists: [],
			colorists: [],
			inkers: [],
			translators: [],
			publisher: {} as Publisher,
			collection: {} as Collection,
			binding: {} as Binding,
			prices: [],
			cover: {} as Image
		}
	}

	// Load collections with the currently available data
	loadCollections(album)

	// Check the edition dates
	sameEditionDates.value = (album.firstEditionDate
		&& album.firstEditionDate === album.currentEditionDate) || false

	return Promise.resolve(album)
}

const {model: album, loading, save, reset} = useSimpleEdit(fetchData, albumService,
	[loadAuthors, loadBindings, loadImages, loadPublishers, loadSeries, loadTags],
	'title.add.album', 'title.edit.album', 'AlbumView')

function adjustEditionDates() {
	if (album.value.markedAsFirstEdition) {
		sameEditionDates.value = true
	}
	if (sameEditionDates.value) {
		album.value.currentEditionDate = album.value.firstEditionDate
	}
}

const rules =  {
	cycle: [integer()],
	number: [number()],
	title: [mandatory()],
	publisher: [mandatory()],
	isbn: [isbn()]
}
</script>
