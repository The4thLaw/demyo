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
			<FieldValue :label="$t('field.Series.website')" :value="series.website">
				<a :href="series.website">{{ series.website }}</a>
			</FieldValue>

			<FieldValue :label="$t('field.Series.completed.view')" :value="true">
				<template v-if="series.completed">
					{{ $t('field.Series.completed.value.true') }}
				</template>
				<template v-if="!series.completed">
					{{ $t('field.Series.completed.value.false') }}
				</template>
			</FieldValue>

			<FieldValue :label="$t('field.Series.albumCount')" :value="true">
				TODO: field.Series.albumCount.count.full and field.Series.albumCount.count.partial
			</FieldValue>

			<FieldValue :label="$t('field.Series.location')" :value="series.location">
				{{ series.location }}
			</FieldValue>

			TODO: authors, tags
			<v-row>
				<v-col cols="12" md="6" v-if="series.biography">
					<FieldValue :label="$t('field.Series.summary')" :value="series.biography">
						<div v-html="series.summary" />
					</FieldValue>
				</v-col>
				<v-col cols="12" md="6">
					<FieldValue :label="$t('field.Series.comment')" :value="series.comment">
						<div v-html="series.comment" />
					</FieldValue>
				</v-col>
			</v-row>
		</SectionCard>

		<SectionCard v-if="!loading && series.albumIds" :title="$t('field.Series.albums')">
			<v-switch v-model="showWishlist" :label="$t('page.Series.showWishlist')" prepend-icon="mdi-gift" />
			<v-row>
				<v-col v-for="albumId in series.albumIds" :key="albumId" cols="12" md="6">
					<AlbumCard :album="albums[albumId]" :loading="albums[albumId].loading" />
				</v-col>
			</v-row>
		</SectionCard>
	</v-container>
</template>

<script>
// TODO: albums
// TODO: derivatives
// TODO: QT for: fave/unfave, add to reading list (if not already all in it), delete if no albums or derivatives
// TODO: QT for: add deriv to series, tag all albums if at least one album, remove a tag if at least one album
// TODO: show/hide wishlist items only if there are wishlist items
import AlbumCard from '@/components/AlbumCard'
import AppTask from '@/components/AppTask'
import AppTasks from '@/components/AppTasks'
import FieldValue from '@/components/FieldValue'
import SectionCard from '@/components/SectionCard'
import { deleteStub } from '@/helpers/actions'
import modelViewMixin from '@/mixins/model-view'
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

	methods: {
		async fetchData() {
			this.series = await seriesService.findById(this.parsedId)

			if (this.series.albumIds) {
				this.series.albumIds.forEach(id => {
					this.albums[id] = { loading: true }
				})
			}
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
