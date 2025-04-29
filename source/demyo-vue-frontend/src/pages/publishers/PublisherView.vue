<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.publisher')"
				:to="`/publishers/${publisher.id}/edit`"
				icon="mdi-office-building dem-overlay-edit"
			/>
			<AppTask
				v-if="albumCount === 0 && collectionCount === 0"
				:label="$t('quickTasks.delete.publisher')"
				:confirm="$t('quickTasks.delete.publisher.confirm')"
				icon="mdi-office-building dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteModel"
			/>
			<AppTask
				:label="$t('quickTasks.add.collection.to.publisher')"
				:to="{ name: 'CollectionAdd', query: { toPublisher: publisher.id } }"
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
				<!-- eslint-disable-next-line vue/no-v-html -->
				<div v-html="publisher.history" />
			</FieldValue>

			<v-btn
				v-if="albumCount > 0"
				:to="{ name: 'AlbumIndex', query: { withPublisher: publisher.id } }"
				color="secondary" class="my-4" size="small" variant="outlined"
			>
				{{ $t('page.Publisher.viewAlbums', albumCount) }}
			</v-btn>
			<v-alert
				v-if="albumCount === 0"
				border="start" type="info" text class="my-4"
			>
				{{ $t('page.Publisher.noAlbums') }}
			</v-alert>
		</SectionCard>

		<SectionCard
			v-if="loading || collectionCount > 0" :loading="loading"
			:title="$t('field.Publisher.collections')"
		>
			<TextIndex
				view-route="CollectionView" :items="publisher.collections"
				:split-by-first-letter="false" compact
			/>
		</SectionCard>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleView } from '@/composables/model-view'
import publisherService from '@/services/publisher-service'

const albumCount = ref(-1)

async function fetchData(id: number): Promise<Publisher> {
	const publisherP = publisherService.findById(id)
	albumCount.value = await publisherService.countAlbums(id)
	return publisherP
}

const { model: publisher, loading, appTasksMenu, deleteModel } = useSimpleView(fetchData,
	publisherService, 'quickTasks.delete.publisher.confirm.done', 'PublisherIndex')

const collectionCount = computed(() => publisher.value?.collections?.length || 0)
</script>
