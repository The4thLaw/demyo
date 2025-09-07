<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.collection')"
				:to="`/collections/${collection.id}/edit`"
				icon="mdi-folder-multiple dem-overlay-edit"
			/>
			<AppTask
				:label="$t('quickTasks.add.image.to.collection')"
				icon="mdi-camera dem-overlay-add"
				@click="appTasksMenu = false; dndDialog = true"
			/>
			<AppTask
				v-if="albumCount === 0"
				:label="$t('quickTasks.delete.collection')"
				:confirm="$t('quickTasks.delete.collection.confirm')"
				icon="mdi-folder-multiple dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteModel"
			/>
		</AppTasks>
		<DnDImage v-model="dndDialog" main-image-label="field.Collection.logo" @save="saveDndImages" />

		<SectionCard :loading="loading" :image="collection.logo" :title="collection.identifyingName">
			<FieldValue
				:value="collection.publisher" label-key="field.Collection.publisher" type="PublisherView"
			/>
			<v-row>
				<FieldValue
					:value="collection.website" label-key="field.Collection.website"
					type="url" cols="12" md="6"
				/>
				<FieldValue
					:value="collection.feed" label-key="field.Collection.feed"
					type="url" cols="12" md="6"
				/>
			</v-row>
			<FieldValue :value="collection.history" label-key="field.Collection.history" type="rich-text" />
			<v-btn
				v-if="albumCount > 0"
				:to="{ name: 'AlbumIndex', query: { withCollection: collection.id } }"
				color="secondary" class="my-4" size="small" variant="outlined"
			>
				{{ $t('page.Collection.viewAlbums', albumCount) }}
			</v-btn>
			<v-alert
				v-if="albumCount === 0"
				border="start" type="info" text class="my-4"
				variant="outlined"
			>
				{{ $t('page.Collection.noAlbums') }}
			</v-alert>
		</SectionCard>
	</v-container>
</template>

<script setup lang="ts">
import { useDndImages } from '@/composables/dnd-images'
import { useSimpleView } from '@/composables/model-view'
import collectionService from '@/services/collection-service'

const albumCount = ref(-1)

async function fetchData(id: number): Promise<Collection> {
	const collectionP = collectionService.findById(id)
	albumCount.value = await collectionService.countAlbums(id)
	return collectionP // Resolve calls in parallel
}

const { model: collection, loading, loadData, appTasksMenu, deleteModel }
	= useSimpleView(fetchData, collectionService, 'quickTasks.delete.collection.confirm.done', 'PublisherIndex',
		c => c ? `${c.identifyingName} – ${c.publisher?.identifyingName}` : '')

const { dndDialog, saveDndImages } = useDndImages(
	async (data: FilePondData) => collectionService.saveFilepondImages(collection.value.id, data.mainImage),
	loadData
)
</script>
