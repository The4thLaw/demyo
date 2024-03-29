<template>
	<v-container fluid>
		<portal v-if="!loading" to="appBarAddons">
			<FavouriteButton :model-id="series.id" type="Series" />
		</portal>

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
				@confirm="deleteSeries"
			/>
			<AppTask
				v-if="hasAlbumsOutsideReadingList"
				:label="$t('quickTasks.add.series.to.readingList')"
				icon="mdi-library"
				@click="addSeriesToReadingList"
			/>
			<AppTask
				:label="$t('quickTasks.add.album.to.series')"
				:to="{ name: 'AlbumAdd', query: { toSeries: series.id }}"
				icon="mdi-book-open-variant dem-overlay-add"
			/>
			<AppTask
				:label="$t('quickTasks.add.derivative.to.series')"
				:to="{ name: 'DerivativeAdd', query: derivativeQuery}"
				icon="mdi-image-frame dem-overlay-add"
			/>
		</AppTasks>

		<SectionCard :loading="loading" :title="series.identifyingName">
			<div v-if="series.originalName" class="c-SeriesView__originalName">
				({{ series.originalName }})
			</div>
			<FieldValue v-if="series.website" :label="$t('field.Series.website')">
				<a :href="series.website">{{ series.website }}</a>
			</FieldValue>

			<FieldValue :label="$t('field.Series.completed.view')">
				{{ completionLabel }}
			</FieldValue>

			<FieldValue v-if="series.location" :label="$t('field.Series.location')">
				{{ series.location }}
			</FieldValue>

			<FieldValue
				v-if="series.relatedSeries && series.relatedSeries.length > 0"
				:label="$t('field.Series.relatedSeries')"
			>
				<ModelLink :model="series.relatedSeries" view="SeriesView" />
			</FieldValue>

			<div class="dem-columnized">
				<FieldValue v-if="series.summary" :label="$t('field.Series.summary')">
					<!-- eslint-disable-next-line vue/no-v-html -->
					<div v-html="series.summary" />
				</FieldValue>
				<FieldValue v-if="series.comment" :label="$t('field.Series.comment')">
					<!-- eslint-disable-next-line vue/no-v-html -->
					<div v-html="series.comment" />
				</FieldValue>
			</div>
		</SectionCard>

		<SectionCard
			v-if="loading || series.albumIds || derivativeCount > 0"
			ref="contentSection" class="c-SectionCard--tabbed"
		>
			<v-tabs v-model="currentTab" background-color="primary" dark grow>
				<v-tab :disabled="albumsLoaded && albumCount <= 0">
					<v-icon left>
						mdi-book-open-variant
					</v-icon>
					{{ $t('field.Series.albums') }}
				</v-tab>
				<v-tab :disabled="derivativeCount <= 0" @change="loadDerivatives">
					<v-icon left>
						mdi-image-frame
					</v-icon>
					{{ $tc('field.Series.derivatives', derivativeCount) }}
				</v-tab>

				<!-- Albums -->
				<v-tab-item class="dem-tab">
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
							v-if="allPublishers.length" :label="$tc('field.Album.publisher', allPublishers.length)"
						>
							<ModelLink :model="allPublishers" view="PublisherView" />
						</FieldValue>

						<FieldValue v-if="allWriters.length" :label="$tc('field.Album.writers', allWriters.length)">
							<ModelLink :model="allWriters" view="AuthorView" />
						</FieldValue>
						<FieldValue v-if="allArtists.length" :label="$tc('field.Album.artists', allArtists.length)">
							<ModelLink :model="allArtists" view="AuthorView" />
						</FieldValue>
						<FieldValue
							v-if="allColorists.length" :label="$tc('field.Album.colorists', allColorists.length)"
						>
							<ModelLink :model="allColorists" view="AuthorView" />
						</FieldValue>
						<FieldValue v-if="allInkers.length" :label="$tc('field.Album.inkers', allInkers.length)">
							<ModelLink :model="allInkers" view="AuthorView" />
						</FieldValue>
						<FieldValue
							v-if="allTranslators.length" :label="$tc('field.Album.translators', allTranslators.length)"
						>
							<ModelLink :model="allTranslators" view="AuthorView" />
						</FieldValue>

						<FieldValue v-if="allTags.length" :label="$tc('field.Album.tags', allTags.length)">
							<TagLink :model="allTags" />
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
								:album="albums[albumId]" :loading="albums[albumId].loading"
								:load-cover="albumsLoaded"
							/>
						</v-col>
					</v-row>
				</v-tab-item>

				<!-- Derivatives -->
				<v-tab-item class="dem-tab">
					<div v-if="derivatives.length <= 0" class="text-center">
						<v-progress-circular indeterminate color="primary" size="64" />
					</div>
					<GalleryIndex
						:items="derivatives" image-path="mainImage" bordered
						@page-change="$refs.contentSection.$el.scrollIntoView()"
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
				</v-tab-item>
			</v-tabs>
		</SectionCard>
	</v-container>
</template>

<script>
import AlbumCard from '@/components/AlbumCard.vue'
import AppTask from '@/components/AppTask.vue'
import AppTasks from '@/components/AppTasks.vue'
import FavouriteButton from '@/components/FavouriteButton.vue'
import FieldValue from '@/components/FieldValue.vue'
import GalleryIndex from '@/components/GalleryIndex.vue'
import ModelLink from '@/components/ModelLink.vue'
import SectionCard from '@/components/SectionCard.vue'
import TagLink from '@/components/TagLink.vue'
import { deleteStub } from '@/helpers/actions'
import { mergeModels } from '@/helpers/fields'
import modelViewMixin from '@/mixins/model-view'
import albumService from '@/services/album-service'
import derivativeService from '@/services/derivative-service'
import readerService from '@/services/reader-service'
import seriesService from '@/services/series-service'
import { useReaderStore } from '@/stores/reader'
import sortedIndexOf from 'lodash/sortedIndexOf'
import { mapState } from 'pinia'
import asyncPool from 'tiny-async-pool'
import Vue from 'vue'

export default {
	name: 'SeriesView',

	components: {
		AlbumCard,
		AppTask,
		AppTasks,
		FavouriteButton,
		FieldValue,
		GalleryIndex,
		ModelLink,
		SectionCard,
		TagLink
	},

	mixins: [modelViewMixin],

	metaInfo() {
		return {
			title: this.series.identifyingName
		}
	},

	data() {
		return {
			series: {},
			albums: {},
			albumsLoaded: false,
			derivatives: [],
			derivativeCount: -1,
			showWishlist: true,
			appTasksMenu: false,
			currentTab: 0
		}
	},

	computed: {
		ownedIds() {
			if (!this.series.albumIds) {
				return []
			}
			return this.series.albumIds.filter(id => {
				const album = this.albums[id]
				if (!album) {
					return false
				}
				return album.loading || !album.wishlist
			})
		},

		filteredIds() {
			if (this.showWishlist) {
				return this.series.albumIds
			}
			return this.ownedIds
		},

		albumCount() {
			return this.series.albumIds ? this.series.albumIds.length : 0
		},

		ownedAlbumCount() {
			return this.ownedIds.length
		},

		allPublishers() {
			return this.albumsLoaded ? mergeModels(this.albums, 'publisher', 'identifyingName') : []
		},

		allWriters() {
			return this.albumsLoaded ? mergeModels(this.albums, 'writers', ['name', 'firstName']) : []
		},

		allArtists() {
			return this.albumsLoaded ? mergeModels(this.albums, 'artists', ['name', 'firstName']) : []
		},

		allColorists() {
			return this.albumsLoaded ? mergeModels(this.albums, 'colorists', ['name', 'firstName']) : []
		},

		allInkers() {
			return this.albumsLoaded ? mergeModels(this.albums, 'inkers', ['name', 'firstName']) : []
		},

		allTranslators() {
			return this.albumsLoaded ? mergeModels(this.albums, 'translators', ['name', 'firstName']) : []
		},

		allTags() {
			return this.albumsLoaded ? mergeModels(this.albums, 'tags', 'identifyingName') : []
		},

		authorsAlive() {
			const relevantAuthors = [...this.allWriters, ...this.allArtists]
			return relevantAuthors.length === 0 || relevantAuthors.some(a => !a.deathDate)
		},

		completionLabel() {
			if (this.series.completed) {
				if (!this.minAlbumYear || !this.maxAlbumYear) {
					return this.$t('field.Series.completed.value.trueNoDates')
				}
				return this.$t('field.Series.completed.value.true', { min: this.minAlbumYear, max: this.maxAlbumYear })
			}

			if (this.authorsAlive) {
				if (!this.minAlbumYear) {
					return this.$t('field.Series.completed.value.falseNoDates')
				}
				return this.$t('field.Series.completed.value.false', { min: this.minAlbumYear })
			}

			if (!this.minAlbumYear) {
				return this.$t('page.Series.allAuthorsDeceased.withoutDate')
			}
			return this.$t('page.Series.allAuthorsDeceased.withDate', { min: this.minAlbumYear })
		},

		derivativeQuery() {
			const query = {
				toSeries: this.series.id
			}
			if (this.allArtists.length === 1) {
				query.toArtist = this.allArtists[0].id
			}
			// If not all albums are loaded, let impatient users add the Derivative just to the Series
			return query
		},

		hasAlbumsOutsideReadingList() {
			if (!this.albumsLoaded) {
				return false
			}

			return Object.values(this.albums).some(a => sortedIndexOf(this.readingList, a.id) <= -1)
		},

		albumDates() {
			if (!this.albumsLoaded) {
				return []
			}

			return Object.values(this.albums)
				.map(a => a.firstEditionDate || a.currentEditionDate || a.printingDate)
				.filter(Boolean)
				.map(d => new Date(d))
		},

		minAlbumYear() {
			if (!this.albumDates.length) {
				return null
			}

			const minDate = new Date(Math.min.apply(null, this.albumDates))
			return minDate.getFullYear()
		},

		maxAlbumYear() {
			if (!this.albumDates.length) {
				return null
			}
			if (!this.series.completed) {
				return null
			}

			const maxDate = new Date(Math.max.apply(null, this.albumDates))
			return maxDate.getFullYear()
		},

		...mapState(useReaderStore, {
			readingList: store => store.readingList
		})
	},

	methods: {
		async fetchData() {
			this.series = {}
			this.albums = {}
			this.albumsLoaded = false
			this.derivatives = []
			this.derivativeCount = 0

			const dcPromise = seriesService.countDerivatives(this.parsedId)

			this.series = await seriesService.findById(this.parsedId)

			if (this.series.albumIds) {
				this.series.albumIds.forEach(id => {
					Vue.set(this.albums, id, { loading: true })
				})
			}

			// This is intentionnally async
			this.loadAlbums()

			this.derivativeCount = await dcPromise

			if (this.albumCount === 0 && this.derivativeCount > 0) {
				// If there are no albums but there are derivatives, load the derivatives
				this.currentTab = 1
				this.loadDerivatives()
			}
		},

		async loadAlbums() {
			if (this.series.albumIds) {
				for await (const value of asyncPool(2, this.series.albumIds, this.albumLoader)) {
  					// Nothing to do
				}
			}
			this.albumsLoaded = true
		},

		async albumLoader(id) {
			const album = await albumService.findById(id)
			this.albums[id] = album
			this.albums[id].loading = false
		},

		deleteSeries() {
			deleteStub(this,
				() => seriesService.deleteModel(this.series.id),
				'quickTasks.delete.series.confirm.done',
				'SeriesIndex')
		},

		addSeriesToReadingList() {
			readerService.addSeriesToReadingList(this.parsedId)
		},

		async loadDerivatives() {
			if (this.derivatives.length > 0) {
				// Don't reload every time
				return
			}
			this.derivatives = await derivativeService.findForIndex({ series: this.parsedId })
		}
	}
}
</script>

<style lang="less">
.c-SeriesView__originalName {
	margin-top: -1em;
	opacity: 0.87;
}

.c-SeriesView__albumAggregateData {
	border-bottom: 1px solid var(--dem-base-border);
	margin-bottom: 1.5em;
}
</style>
