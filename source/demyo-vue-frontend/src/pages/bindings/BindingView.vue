<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.binding')"
				:to="`/bindings/${binding.id}/edit`"
				icon="mdi-brush dem-overlay-edit"
			/>
			<AppTask
				v-if="albumCount === 0"
				:label="$t('quickTasks.delete.binding')"
				:confirm="$t('quickTasks.delete.binding.confirm')"
				icon="mdi-brush dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteModel"
			/>
		</AppTasks>

		<SectionCard :loading="loading" :title="binding.identifyingName">
			<v-btn
				v-if="albumCount > 0"
				:to="{ name: 'AlbumIndex', query: { withBinding: binding.id } }"
				color="secondary" class="my-4" size="small" variant="outlined"
			>
				{{ $t('page.Binding.viewAlbums', albumCount) }}
			</v-btn>
			<v-alert
				v-if="albumCount === 0"
				border="start" type="info" text class="my-4"
			>
				{{ $t('page.Binding.noAlbums') }}
			</v-alert>
		</SectionCard>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleView } from '@/composables/model-view'
import bindingService from '@/services/binding-service'

const albumCount = ref(-1)

async function fetchData(id: number): Promise<Binding> {
	const bindingP = bindingService.findById(id)
	albumCount.value = await bindingService.countAlbums(id)
	return bindingP // Resolve calls in parallel
}

const { model: binding, loading, appTasksMenu, deleteModel }
	= useSimpleView(fetchData, bindingService, 'quickTasks.delete.binding.confirm.done', 'TypeIndex')
</script>
