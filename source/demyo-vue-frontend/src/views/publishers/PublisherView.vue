<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.publisher')"
				:to="`/publishers/${publisher.id}/edit`"
				icon="mdi-brush dem-overlay-edit"
			/>
			<AppTask
				v-if="count == 0"
				:label="$t('quickTasks.delete.publisher')"
				:confirm="$t('quickTasks.delete.publisher.confirm')"
				icon="mdi-brush dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deletePublisher"
			/>
			<!-- TODO: handle this -->
			<AppTask
				:label="$t('quickTasks.add.collection.to.publisher')"
				:to="{ name: 'CollectionAdd', query: { toPublisher: publisher.id }}"
				icon="mdi-folder-multiple dem-overlay-add"
			/>
		</AppTasks>

		<SectionCard :loading="loading" :image="publisher.logo" :title="publisher.identifyingName">
			<FieldValue v-if="publisher.website" :label="$t('field.Publisher.website')">
				<a :href="publisher.website">{{ publisher.website }}</a>
			</FieldValue>
			<FieldValue v-if="publisher.feed" :label="$t('field.Publisher.feed')">
				<a :href="publisher.feed">{{ publisher.feed }}</a>
			</FieldValue>
			<FieldValue v-if="publisher.history" :label="$t('field.Publisher.history')">
				<div v-html="publisher.history" />
			</FieldValue>

			<v-btn
				v-if="count > 0"
				:to="{ name: 'AlbumIndex', query: { withPublisher: publisher.id } }"
				color="accent" class="my-4" small outlined
			>
				{{ $tc('page.Publisher.viewAlbums', count) }}
			</v-btn>
			<v-alert
				v-if="count == 0"
				border="left" type="info" text class="my-4"
			>
				{{ $t('page.Publisher.noAlbums') }}
			</v-alert>

			<!-- TODO: collections (field.Publisher.collections) -->
		</SectionCard>
	</v-container>
</template>

<script>
import AppTask from '@/components/AppTask'
import AppTasks from '@/components/AppTasks'
import FieldValue from '@/components/FieldValue'
import SectionCard from '@/components/SectionCard'
import { deleteStub } from '@/helpers/actions'
import modelViewMixin from '@/mixins/model-view'
import publisherService from '@/services/publisher-service'

export default {
	name: 'PublisherView',

	components: {
		AppTask,
		AppTasks,
		FieldValue,
		SectionCard
	},

	mixins: [modelViewMixin],

	metaInfo() {
		return {
			title: this.publisher.identifyingName
		}
	},

	data() {
		return {
			publisher: {},
			count: -1,
			appTasksMenu: false
		}
	},

	methods: {
		async fetchData() {
			let publisherP = publisherService.findById(this.parsedId)
			this.count = await publisherService.countAlbums(this.parsedId)
			this.publisher = await publisherP // Resolve calls in parallel
		},

		deletePublisher() {
			deleteStub(this,
				() => publisherService.deleteModel(this.publisher.id),
				'quickTasks.delete.publisher.confirm.done',
				'PublisherIndex')
		}
	}
}
</script>
