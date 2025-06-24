<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.universe')" :to="`/universes/${universe.id}/edit`"
				icon="mdi-earth dem-overlay-edit"
			/>
			<AppTask
				:label="$t('quickTasks.delete.universe')"
				:confirm="$t('quickTasks.delete.universe.confirm')"
				icon="mdi-earth dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteModel"
			/>
		</AppTasks>

		<SectionCard :loading="universeLoading" :image="universe.logo" :title="universe.identifyingName">
			TODO[universe]: depends on #225
		</SectionCard>

		<SectionCard
			v-if="albumsLoading || albumCount > 0"
			:loading="albumsLoading"
			:title="$t('page.Universe.content', albumCount)"
		>
			<AlbumTextList :albums="albums" />
		</SectionCard>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleView } from '@/composables/model-view'
import universeService from '@/services/universe-service'

const universeLoading = ref(true)
const albumsLoading = ref(true)
const albums = ref([] as Album[])

async function fetchData(id: number): Promise<Universe> {
	universeLoading.value = true
	albumsLoading.value = true

	const universeP = await universeService.findById(id)
	universeLoading.value = false

	albums.value = await universeService.getUniverseContents(id)
	albumsLoading.value = false

	return universeP
}

const { model: universe, loading, appTasksMenu, deleteModel } = useSimpleView(fetchData, universeService,
	'quickTasks.delete.universe.confirm.done', 'UniverseIndex')

const albumCount = computed(() => albums.value.length)
</script>
