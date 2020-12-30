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
				color="accent" class="my-4" small outlined
			>
				{{ $tc('page.Collection.viewAlbums', count) }}
			</v-btn>
			<v-alert
				v-if="count === 0"
				border="left" type="info" text class="my-4"
			>
				{{ $t('page.Collection.noAlbums') }}
			</v-alert>
		</SectionCard>
	</v-container>
</template>

<script>
import AppTask from '@/components/AppTask'
import AppTasks from '@/components/AppTasks'
import FieldValue from '@/components/FieldValue'
import ModelLink from '@/components/ModelLink'
import SectionCard from '@/components/SectionCard'
import { deleteStub } from '@/helpers/actions'
import modelViewMixin from '@/mixins/model-view'
import collectionService from '@/services/collection-service'

export default {
	name: 'CollectionView',

	components: {
		AppTask,
		AppTasks,
		FieldValue,
		ModelLink,
		SectionCard
	},

	mixins: [modelViewMixin],

	metaInfo() {
		return {
			title: this.collection.identifyingName
		}
	},

	data() {
		return {
			collection: {},
			count: -1,
			appTasksMenu: false
		}
	},

	methods: {
		async fetchData() {
			const collectionP = collectionService.findById(this.parsedId)
			this.count = await collectionService.countAlbums(this.parsedId)
			this.collection = await collectionP // Resolve calls in parallel
		},

		deleteCollection() {
			deleteStub(this,
				() => collectionService.deleteModel(this.collection.id),
				'quickTasks.delete.collection.confirm.done',
				'PublisherIndex')
		}
	}
}
</script>
