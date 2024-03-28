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
				@confirm="deleteAlbum"
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
							<FieldValue v-if="album.depth" :label="$t('field.Album.depth')">
								{{ album.depth }}
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
							{{ album.purchasePrice | price(currency) }}
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
			v-if="derivativeCount > 0" ref="derivativeSection" v-intersect="loadDerivatives"
			:title="$t('field.Album.derivatives')"
		>
			<div v-if="derivativesLoading" class="text-center">
				<v-progress-circular indeterminate color="primary" size="64" />
			</div>
			<GalleryIndex
				:items="derivatives" image-path="mainImage" bordered
				@page-change="$refs.derivativeSection.$el.scrollIntoView()"
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

<script>
import AppTask from '@/components/AppTask.vue'
import AppTasks from '@/components/AppTasks.vue'
import DnDImage from '@/components/DnDImage.vue'
import FavouriteButton from '@/components/FavouriteButton.vue'
import FieldValue from '@/components/FieldValue.vue'
import GalleryIndex from '@/components/GalleryIndex.vue'
import ModelLink from '@/components/ModelLink.vue'
import PriceTable from '@/components/PriceTable.vue'
import SectionCard from '@/components/SectionCard.vue'
import TagLink from '@/components/TagLink.vue'
import { deleteStub } from '@/helpers/actions'
import i18nMixin from '@/mixins/i18n'
import modelViewMixin from '@/mixins/model-view'
import albumService from '@/services/album-service'
import derivativeService from '@/services/derivative-service'
import readerService from '@/services/reader-service'
import { useReaderStore } from '@/stores/reader'
import { useUiStore } from '@/stores/ui'
import sortedIndexOf from 'lodash/sortedIndexOf'
import { mapState } from 'pinia'

export default {
	name: 'AlbumView',

	components: {
		AppTask,
		AppTasks,
		DnDImage,
		FavouriteButton,
		FieldValue,
		GalleryIndex,
		ModelLink,
		PriceTable,
		SectionCard,
		TagLink
	},

	mixins: [i18nMixin, modelViewMixin],

	data() {
		return {
			uiStore: useUiStore(),

			appTasksMenu: false,
			dndDialog: false,
			album: {
				series: {},
				publisher: {},
				collection: {},
				binding: {}
			},
			derivativeCount: 0,
			inhibitObserver: true,
			derivativesLoading: false,
			derivatives: []
		}
	},

	head() {
		return {
			title: this.album.title
		}
	},

	computed: {
		hasAuthors() {
			return this.album.writers?.length
				|| this.album.artists?.length
				|| this.album.colorists?.length
				|| this.album.inkers?.length
				|| this.album.translators?.length
		},

		hasPrices() {
			return this.album.prices?.length
		},

		hasImages() {
			return this.album.images?.length
		},

		sizeSpec() {
			if (this.album.width && this.album.height) {
				return `${this.album.width} x ${this.album.height}`
			}

			return null
		},

		derivativeQuery() {
			const query = {
				toAlbum: this.album.id
			}
			if (this.album.series) {
				query.toSeries = this.album.series.id
			}
			if (this.album.artists?.length === 1) {
				query.toArtist = this.album.artists[0].id
			}
			return query
		},

		...mapState(useReaderStore, {
			isInReadingList: function (store) {
				return sortedIndexOf(store.readingList, this.album.id) > -1
			}
		})
	},

	methods: {
		async fetchData() {
			const dcPromise = albumService.countDerivatives(this.parsedId)
			this.album = await albumService.findById(this.parsedId)
			this.derivativeCount = await dcPromise
			// If we enable the v-intersect observer immediately, it will be triggered on page load as well
			// Probably because the page fills too slowly
			setTimeout(() => {
				this.inhibitObserver = false
			}, 500)
		},

		async saveDndImages(data) {
			const ok = await albumService.saveFilepondImages(this.album.id, data.mainImage, data.otherImages)
			if (ok) {
				this.uiStore.showSnackbar(this.$t('draganddrop.snack.confirm'))
				this.fetchDataInternal()
			} else {
				this.uiStore.showSnackbar(this.$t('core.exception.api.title'))
			}
		},

		deleteAlbum() {
			deleteStub(this,
				() => albumService.deleteModel(this.album.id),
				'quickTasks.delete.album.confirm.done',
				'AlbumIndex')
		},

		async loadDerivatives() {
			if (this.inhibitObserver || this.derivativeCount <= 0) {
				// The page isn't loaded yet. Don't do anything
				return
			}
			if (this.derivativesLoading || this.derivatives.length > 0) {
				// The page is loading or has loaded. Don't do anything
				return
			}
			this.derivativesLoading = true
			this.derivatives = await derivativeService.findForIndex({ album: this.album.id })
			this.derivativesLoading = false
		},

		addToReadingList() {
			readerService.addToReadingList(this.album.id)
		},

		markAsRead() {
			readerService.removeFromReadingList(this.album.id)
		}
	}
}
</script>

<style lang="less">
.c-AlbumView__originalTitle {
	margin-top: -1em;
	margin-bottom: 16px;
	opacity: 0.87;
}
</style>
