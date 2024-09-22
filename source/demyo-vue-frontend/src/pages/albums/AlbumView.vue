<template>
	<v-container fluid>
		<Teleport v-if="!loading" to="#teleport-appBarAddons">
			<FavouriteButton :model-id="album.id" type="Album" />
		</Teleport>

		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.album')"
				:to="`/albums/${album.id}/edit`"
				icon="mdi-book-open-variant dem-overlay-edit"
			/>
			<!--
			Adding an @click="appTasksMenu = false" causes the dialog to instantly
			disappear because the AppTask isn't rendered anymore
			-->
			<AppTask
				v-if="!loading && derivativeCount <= 0"
				:label="$t('quickTasks.delete.album')"
				:confirm="$t('quickTasks.delete.album.confirm')"
				icon="mdi-book-open-variant dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteModel"
			/>
			<AppTask
				:label="$t('quickTasks.add.images.to.album')"
				icon="mdi-camera dem-overlay-add"
				@click="appTasksMenu = false; dndDialog = true"
			/>
			<AppTask
				v-if="!isInReadingList"
				:label="$t('quickTasks.add.album.to.readingList')"
				icon="mdi-library dem-overlay-add"
				@click="appTasksMenu = false; addToReadingList()"
			/>
			<AppTask
				:label="$t('quickTasks.add.derivative.to.album')"
				:to="{ name: 'DerivativeAdd', query: derivativeQuery}"
				icon="mdi-image-frame dem-overlay-add"
			/>
		</AppTasks>
		<DnDImage
			v-model="dndDialog" main-image-label="field.Album.cover"
			other-images-label="field.Album.images" @save="saveDndImages"
		/>

		<SectionCard :loading="loading" :image="album.cover" :title="album.identifyingName">
			<div class="dem-fieldset">
				<div v-if="album.originalTitle" class="c-AlbumView__originalTitle">
					({{ album.originalTitle }})
				</div>
				<v-row>
					<v-col v-if="album.series.id" cols="12" md="6">
						<FieldValue :label="$t('field.Album.series')">
							<ModelLink :model="album.series" view="SeriesView" />
						</FieldValue>
					</v-col>
					<v-col v-if="album.tags && album.tags.length > 0" cols="12">
						<FieldValue :label="$t('field.Album.tags', album.tags.length)">
							<TagLink :model="album.tags" />
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div class="dem-fieldset">
				<v-row>
					<v-col v-if="album.publisher.id" cols="12" md="6">
						<FieldValue :label="$t('field.Album.publisher', 1)">
							<ModelLink :model="album.publisher" view="PublisherView" />
						</FieldValue>
					</v-col>
					<v-col v-if="album.collection.id" cols="12" md="6">
						<FieldValue :label="$t('field.Album.collection')">
							<ModelLink :model="album.collection" view="CollectionView" />
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div v-if="hasAuthors" class="dem-fieldset">
				<v-row>
					<v-col v-if="album.writers && album.writers.length" cols="12" md="6">
						<FieldValue :label="$t('field.Album.writers', album.writers.length)">
							<ModelLink :model="album.writers" view="AuthorView" />
						</FieldValue>
					</v-col>
					<v-col v-if="album.artists && album.artists.length" cols="12" md="6">
						<FieldValue :label="$t('field.Album.artists', album.artists.length)">
							<ModelLink :model="album.artists" view="AuthorView" />
						</FieldValue>
					</v-col>
					<v-col v-if="album.colorists && album.colorists.length" cols="12" md="6">
						<FieldValue :label="$t('field.Album.colorists', album.colorists.length)">
							<ModelLink :model="album.colorists" view="AuthorView" />
						</FieldValue>
					</v-col>
					<v-col v-if="album.inkers && album.inkers.length" cols="12" md="6">
						<FieldValue :label="$t('field.Album.inkers', album.inkers.length)">
							<ModelLink :model="album.inkers" view="AuthorView" />
						</FieldValue>
					</v-col>
					<v-col v-if="album.translators && album.translators.length" cols="12" md="6">
						<FieldValue :label="$t('field.Album.translators', album.writers.length)">
							<ModelLink :model="album.translators" view="AuthorView" />
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div v-if="album.aggregatedLocation" class="dem-fieldset">
				<v-row>
					<v-col cols="12">
						<FieldValue :label="$t('field.Album.location')">
							{{ album.aggregatedLocation }}
						</FieldValue>
					</v-col>
				</v-row>
			</div>
		</SectionCard>

		<SectionCard :loading="loading" :subtitle="$t('page.Album.aboutEdition')">
			<div
				v-if="album.firstEditionDate || album.currentEditionDate || album.wishlist
					|| album.acquisitionDate || album.isbn"
				class="dem-fieldset"
			>
				<v-row>
					<v-col v-if="album.firstEditionDate" cols="12" md="4">
						<FieldValue
							v-if="album.currentEditionDate === album.firstEditionDate"
							:label="$t('field.Album.currentEditionDate')"
						>
							{{ $d(new Date(album.firstEditionDate), 'long') }}
							{{ $t(album.markedAsFirstEdition ?
								'field.Album.markedAsFirstEdition.view' : 'field.Album.isFirstEdition') }}
						</FieldValue>
						<FieldValue v-else :label="$t('field.Album.firstEditionDate')">
							{{ $d(new Date(album.firstEditionDate), 'long') }}
						</FieldValue>
					</v-col>
					<v-col
						v-if="album.currentEditionDate && album.currentEditionDate !== album.firstEditionDate"
						cols="12" md="4"
					>
						<FieldValue :label="$t('field.Album.currentEditionDate')">
							{{ $d(new Date(album.currentEditionDate), 'long') }}
						</FieldValue>
					</v-col>
					<v-col v-if="album.printingDate" cols="12" md="4">
						<FieldValue :label="$t('field.Album.printingDate')">
							{{ $d(new Date(album.printingDate), 'long') }}
						</FieldValue>
					</v-col>
					<v-col v-if="album.acquisitionDate" cols="12" md="4">
						<FieldValue :label="$t('field.Album.acquisitionDate')">
							{{ $d(new Date(album.acquisitionDate), 'long') }}
						</FieldValue>
					</v-col>
					<v-col v-if="album.wishlist" cols="12" md="4">
						<FieldValue :label="$t('field.Album.wishlist.view')">
							{{ $t('field.Album.wishlist.value.true') }}
						</FieldValue>
					</v-col>
					<v-col v-if="album.isbn" cols="12" md="4">
						<FieldValue :label="$t('field.Album.isbn')">
							{{ album.isbn }}
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div v-if="album.binding.id || sizeSpec || album.pages" class="dem-fieldset">
				<v-row>
					<v-col cols="12" md="4">
						<FieldValue v-if="album.binding.id" :label="$t('field.Album.binding')">
							<ModelLink :model="album.binding" view="BindingView" />
						</FieldValue>
					</v-col>

					<v-col v-if="sizeSpec" cols="12" md="4">
						<FieldValue :label="$t('field.Album.size')">
							{{ sizeSpec }}
						</FieldValue>
					</v-col>

					<template v-if="!sizeSpec">
						<v-col cols="12" md="4">
							<FieldValue v-if="album.height" :label="$t('field.Album.height')">
								{{ album.height }}
							</FieldValue>
						</v-col>
						<v-col cols="12" md="4">
							<FieldValue v-if="album.width" :label="$t('field.Album.width')">
								{{ album.width }}
							</FieldValue>
						</v-col>
					</template>

					<v-col v-if="album.pages" cols="12" md="4">
						<FieldValue :label="$t('field.Album.pages')">
							{{ album.pages }}
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div
				v-if="album.purchasePrice || hasPrices"
				class="dem-fieldset"
			>
				<v-row>
					<v-col v-if="album.purchasePrice" cols="12" md="6">
						<FieldValue :label="$t('field.Album.purchasePrice')">
							{{ qualifiedPurchasePrice }}
						</FieldValue>
					</v-col>
					<v-col v-if="hasPrices" cols="12" md="6">
						<PriceTable :prices="album.prices" model-name="Album" />
					</v-col>
				</v-row>
			</div>
		</SectionCard>

		<SectionCard v-if="album.summary || album.comment">
			<FieldValue v-if="album.summary" :label="$t('field.Album.summary')">
				<!-- eslint-disable-next-line vue/no-v-html -->
				<div v-html="album.summary" />
			</FieldValue>
			<FieldValue v-if="album.comment" :label="$t('field.Album.comment')">
				<!-- eslint-disable-next-line vue/no-v-html -->
				<div v-html="album.comment" />
			</FieldValue>
		</SectionCard>

		<SectionCard v-if="hasImages" :loading="loading" :title="$t('page.Album.gallery')">
			<GalleryIndex :items="album.images">
				<template #default="slotProps">
					<router-link :to="`/images/${slotProps.item.id}/view`">
						{{ slotProps.item.identifyingName }}
					</router-link>
				</template>
			</GalleryIndex>
		</SectionCard>

		<SectionCard
			v-if="derivativeCount > 0" ref="derivative-section" v-intersect="loadDerivatives"
			:title="$t('field.Album.derivatives')"
		>
			<div v-if="derivativesLoading" class="text-center">
				<v-progress-circular indeterminate color="primary" size="64" />
			</div>
			<GalleryIndex
				:items="derivatives" image-path="mainImage" bordered
				@page-change="derivativeSection?.$el.scrollIntoView()"
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
		</SectionCard>

		<Fab v-if="isInReadingList" icon="mdi-library dem-overlay-check" @click="markAsRead" />
	</v-container>
</template>

<script setup lang="ts">
import { useCurrency } from '@/composables/currency'
import { useSimpleView } from '@/composables/model-view'
import albumService from '@/services/album-service'
import derivativeService from '@/services/derivative-service'
import readerService from '@/services/reader-service'
import { useReaderStore } from '@/stores/reader'
import { useUiStore } from '@/stores/ui'
import sortedIndexOf from 'lodash/sortedIndexOf'
import { useTemplateRef } from 'vue'
import { useI18n } from 'vue-i18n'

const dndDialog = ref(false)
const derivativeCount = ref(0)
const inhibitObserver = ref(true)
const derivativesLoading = ref(false)
const derivatives = ref([] as Derivative[])
const derivativeSection = useTemplateRef('derivative-section')

async function fetchData(id: number): Promise<Album> {
	const dcPromise = albumService.countDerivatives(id)
	const albumP = albumService.findById(id)
	derivativeCount.value = await dcPromise

	// If we enable the v-intersect observer immediately, it will be triggered on page load as well
	// Probably because the page fills too slowly
	setTimeout(() => {
		inhibitObserver.value = false
	}, 500)

	return albumP
}

const {model: album, loading, appTasksMenu, deleteModel, loadData}
	= useSimpleView(fetchData, albumService,
		'quickTasks.delete.album.confirm.done', 'AlbumIndex',
		a => a.title)

const hasAuthors = computed(() =>
	album.value.writers?.length
	|| album.value.artists?.length
	|| album.value.colorists?.length
	|| album.value.inkers?.length
	|| album.value.translators?.length)

const hasPrices = computed(() => album.value.prices?.length)
const hasImages = computed(() => album.value.images?.length)

const sizeSpec = computed(() => {
	if (album.value.width && album.value.height) {
		return `${album.value.width} x ${album.value.height}`
	}

	return null
})

const derivativeQuery = computed(() => {
	const query: DerivativeQuery = {
		toAlbum: album.value.id
	}
	if (album.value.series) {
		query.toSeries = album.value.series.id
	}
	if (album.value.artists?.length === 1) {
		query.toArtist = album.value.artists[0].id
	}
	return query
})

const { qualifiedPrice: qualifiedPurchasePrice } = useCurrency(album.value.purchasePrice)

const readerStore = useReaderStore()
const isInReadingList = computed(() => sortedIndexOf(readerStore.readingList, album.value.id) > -1)

const uiStore = useUiStore()
const i18n = useI18n()
async function saveDndImages(data: FilePondData) {
	const ok = await albumService.saveFilepondImages(album.value.id, data.mainImage, data.otherImages)
	if (ok) {
		uiStore.showSnackbar(i18n.t('draganddrop.snack.confirm'))
		// Refresh
		loadData()
	} else {
		uiStore.showSnackbar(i18n.t('core.exception.api.title'))
	}
}

async function loadDerivatives() {
	if (inhibitObserver.value || derivativeCount.value <= 0) {
		// The page isn't loaded yet. Don't do anything
		return
	}
	if (derivativesLoading.value || derivatives.value.length > 0) {
		// The page is loading or has loaded. Don't do anything
		return
	}
	derivativesLoading.value = true
	derivatives.value = await derivativeService.findForIndex({ album: album.value.id })
	derivativesLoading.value = false
}

function addToReadingList() {
	readerService.addToReadingList(album.value.id)
}

function markAsRead() {
	readerService.removeFromReadingList(album.value.id)
}
</script>

<style lang="scss">
.c-AlbumView__originalTitle {
	margin-top: -1em;
	margin-bottom: 16px;
	opacity: 0.87;
}
</style>
