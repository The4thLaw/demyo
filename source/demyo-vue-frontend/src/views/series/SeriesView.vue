<template>
	<v-container fluid>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.series')"
				:to="`/series/${series.id}/edit`"
				icon="mdi-animation dem-overlay-edit"
			/>
			<AppTask
				:label="$t('quickTasks.delete.series')"
				:confirm="$t('quickTasks.delete.series.confirm')"
				icon="mdi-animation dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteSeries"
			/>
			<AppTask
				:label="$t('quickTasks.add.derivative.to.series')"
				:to="{ name: 'DerivativeAdd', query: { toSeries: series.id }}"
				icon="mdi-image-frame dem-overlay-add"
			/>
		</AppTasks>

		<SectionCard :loading="loading" :title="series.identifyingName">
			<FieldValue v-if="series.website" :label="$t('field.Series.website')">
				<a :href="series.website">{{ series.website }}</a>
			</FieldValue>

			<FieldValue :label="$t('field.Series.completed.view')">
				<template v-if="series.completed">
					{{ $t('field.Series.completed.value.true') }}
				</template>
				<template v-if="!series.completed">
					{{ $t('field.Series.completed.value.false') }}
				</template>
			</FieldValue>

			<FieldValue v-if="albumsLoaded" :label="$t('field.Series.albumCount')">
				<template v-if="albumCount === ownedAlbumCount">
					{{ $t('field.Series.albumCount.count.full', albumCount) }}
				</template>
				<template v-else>
					{{ $t('field.Series.albumCount.count.partial', [ownedAlbumCount, albumCount]) }}
				</template>
			</FieldValue>

			<FieldValue v-if="series.location" :label="$t('field.Series.location')">
				{{ series.location }}
			</FieldValue>

			TODO: authors, tags
			<v-row>
				<v-col cols="12" md="6" v-if="series.biography">
					<FieldValue v-if="series.biography" :label="$t('field.Series.summary')">
						<div v-html="series.summary" />
					</FieldValue>
				</v-col>
				<v-col cols="12" md="6">
					<FieldValue v-if="series.comment" :label="$t('field.Series.comment')">
						<div v-html="series.comment" />
					</FieldValue>
				</v-col>
			</v-row>
		</SectionCard>

		<SectionCard v-if="!loading && series.albumIds" :title="$t('field.Series.albums')">
			<v-switch
				v-if="albumCount !== ownedAlbumCount" v-model="showWishlist"
				:label="$t('page.Series.showWishlist')" prepend-icon="mdi-gift"
			/>
			<v-row>
				<v-col v-for="albumId in filteredIds" :key="albumId" cols="12" md="6" lg="4">
					<AlbumCard :album="albums[albumId]" :loading="albums[albumId].loading" />
				</v-col>
			</v-row>
		</SectionCard>
	</v-container>
</template>

<script>
// TODO: derivatives
// TODO: QT for: fave/unfave, add to reading list (if not already all in it), delete if no albums or derivatives
// TODO: QT for: add deriv to series, tag all albums if at least one album, remove a tag if at least one album
// TODO: show/hide wishlist items only if there are wishlist items
import { filter } from 'lodash'
import asyncPool from 'tiny-async-pool'
import Vue from 'vue'
import AlbumCard from '@/components/AlbumCard'
import AppTask from '@/components/AppTask'
import AppTasks from '@/components/AppTasks'
import FieldValue from '@/components/FieldValue'
import SectionCard from '@/components/SectionCard'
import { deleteStub } from '@/helpers/actions'
import modelViewMixin from '@/mixins/model-view'
import albumService from '@/services/album-service'
import seriesService from '@/services/series-service'

export default {
	name: 'SeriesView',

	components: {
		AlbumCard,
		AppTask,
		AppTasks,
		FieldValue,
		SectionCard
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
			showWishlist: true,
			appTasksMenu: false
		}
	},

	computed: {
		ownedIds() {
			return filter(this.series.albumIds, id => {
				let album = this.albums[id]
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
			return this.series.albumIds.length
		},

		ownedAlbumCount() {
			return this.ownedIds.length
		}
	},

	methods: {
		async fetchData() {
			this.series = await seriesService.findById(this.parsedId)

			if (this.series.albumIds) {
				this.series.albumIds.forEach(id => {
					Vue.set(this.albums, id, { loading: true })
				})
			}

			// This is intentionnally async
			this.loadAlbums()
		},

		async loadAlbums() {
			await asyncPool(2, this.series.albumIds, this.albumLoader)
			this.albumsLoaded = true
		},

		async albumLoader(id) {
			let album = await albumService.findById(id)
			this.albums[id] = album
			this.albums[id].loading = false
		},

		deleteSeries() {
			deleteStub(this,
				() => seriesService.deleteModel(this.series.id),
				'quickTasks.delete.series.confirm.done',
				'SeriesIndex')
		}
	}
}
</script>
