<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.publisher')"
				:to="`/publishers/${publisher.id}/edit`"
				icon="mdi-brush dem-overlay-edit"
			/>
			<AppTask
				v-if="albumCount === 0 && collectionCount === 0"
				:label="$t('quickTasks.delete.publisher')"
				:confirm="$t('quickTasks.delete.publisher.confirm')"
				icon="mdi-brush dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deletePublisher"
			/>
			<AppTask
				:label="$t('quickTasks.add.collection.to.publisher')"
				:to="{ name: 'CollectionAdd', query: { toPublisher: publisher.id }}"
				icon="mdi-folder-multiple dem-overlay-add"
			/>
		</AppTasks>

		<SectionCard :loading="loading" :image="publisher.logo" :title="publisher.identifyingName">
			<v-row>
				<v-col v-if="publisher.website" cols="12" md="6">
					<FieldValue :label="$t('field.Publisher.website')">
						<a :href="publisher.website">{{ publisher.website }}</a>
					</FieldValue>
				</v-col>
				<v-col v-if="publisher.feed" cols="12" md="6">
					<FieldValue :label="$t('field.Publisher.feed')">
						<a :href="publisher.feed">{{ publisher.feed }}</a>
					</FieldValue>
				</v-col>
			</v-row>
			<FieldValue v-if="publisher.history" :label="$t('field.Publisher.history')">
				<div v-html="publisher.history" />
			</FieldValue>

			<v-btn
				v-if="albumCount > 0"
				:to="{ name: 'AlbumIndex', query: { withPublisher: publisher.id } }"
				color="accent" class="my-4" small outlined
			>
				{{ $tc('page.Publisher.viewAlbums', albumCount) }}
			</v-btn>
			<v-alert
				v-if="albumCount === 0"
				border="left" type="info" text class="my-4"
			>
				{{ $t('page.Publisher.noAlbums') }}
			</v-alert>
		</SectionCard>

		<SectionCard
			v-if="loading || collectionCount > 0" :loading="loading"
			:title="$t('field.Publisher.collections')"
		>
			<TextIndex :items="publisher.collections" :split-by-first-letter="false" compact>
				<template v-slot:default="slotProps">
					<router-link :to="`/collections/${slotProps.item.id}/view`">
						{{ slotProps.item.identifyingName }}
					</router-link>
				</template>
			</TextIndex>
		</SectionCard>
	</v-container>
</template>

<script>
import AppTask from '@/components/AppTask'
import AppTasks from '@/components/AppTasks'
import FieldValue from '@/components/FieldValue'
import SectionCard from '@/components/SectionCard'
import TextIndex from '@/components/TextIndex'
import { deleteStub } from '@/helpers/actions'
import modelViewMixin from '@/mixins/model-view'
import publisherService from '@/services/publisher-service'

export default {
	name: 'PublisherView',

	components: {
		AppTask,
		AppTasks,
		FieldValue,
		SectionCard,
		TextIndex
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
			albumCount: -1,
			appTasksMenu: false
		}
	},

	computed: {
		collectionCount() {
			if (!this.publisher || !this.publisher.collections) {
				return 0
			}

			return this.publisher.collections.length
		}
	},

	methods: {
		async fetchData() {
			let publisherP = publisherService.findById(this.parsedId)
			this.albumCount = await publisherService.countAlbums(this.parsedId)
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
