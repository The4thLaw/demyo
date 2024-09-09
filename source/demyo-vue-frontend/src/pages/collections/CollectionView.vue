<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.collection')"
				:to="`/collections/${collection.id}/edit`"
				icon="mdi-folder-multiple dem-overlay-edit"
			/>
			<AppTask
				v-if="count === 0"
				:label="$t('quickTasks.delete.collection')"
				:confirm="$t('quickTasks.delete.collection.confirm')"
				icon="mdi-folder-multiple dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteCollection"
			/>
		</AppTasks>

		<SectionCard :loading="loading" :image="collection.logo" :title="collection.identifyingName">
			<FieldValue :label="$t('field.Collection.publisher')">
				<ModelLink :model="collection.publisher" view="PublisherView" />
			</FieldValue>
			<v-row>
				<v-col v-if="collection.website" cols="12" md="6">
					<FieldValue :label="$t('field.Collection.website')">
						<a :href="collection.website">{{ collection.website }}</a>
					</FieldValue>
				</v-col>
				<v-col v-if="collection.feed" cols="12" md="6">
					<FieldValue :label="$t('field.Collection.feed')">
						<a :href="collection.feed">{{ collection.feed }}</a>
					</FieldValue>
				</v-col>
			</v-row>
			<FieldValue v-if="collection.history" :label="$t('field.Collection.history')">
				<!-- eslint-disable-next-line vue/no-v-html -->
				<div v-html="collection.history" />
			</FieldValue>
			<v-btn
				v-if="count > 0"
				:to="{ name: 'AlbumIndex', query: { withCollection: collection.id } }"
				color="secondary" class="my-4" size="small" variant="outlined"
			>
				{{ $t('page.Collection.viewAlbums', count) }}
			</v-btn>
			<v-alert
				v-if="count === 0"
				border="start" type="info" text class="my-4"
			>
				{{ $t('page.Collection.noAlbums') }}
			</v-alert>
		</SectionCard>
	</v-container>
</template>

<script>
import { deleteStub } from '@/helpers/actions'
import modelViewMixin from '@/mixins/model-view'
import collectionService from '@/services/collection-service'

export default {
	name: 'CollectionView',

	mixins: [modelViewMixin],

	data() {
		return {
			collection: {},
			count: -1,
			appTasksMenu: false
		}
	},

	head() {
		return {
			title: this.collection
				? `${this.collection.identifyingName} – ${this.collection.publisher?.identifyingName}` : null
		}
	},

	methods: {
		async fetchData() {
			const collectionP = collectionService.findById(this.parsedId)
			this.count = await collectionService.countAlbums(this.parsedId)
			this.collection = await collectionP // Resolve calls in parallel
		},

		deleteCollection() {
			void deleteStub(this,
				async () => collectionService.deleteModel(this.collection.id),
				'quickTasks.delete.collection.confirm.done',
				'PublisherIndex')
		}
	}
}
</script>
