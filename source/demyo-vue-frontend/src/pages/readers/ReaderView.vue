<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.reader')"
				:to="`/readers/${reader.id}/edit`"
				icon="mdi-account dem-overlay-edit"
			/>
			<AppTask
				:label="$t('quickTasks.configure.reader')"
				:to="`/readers/${reader.id}/configuration`"
				icon="mdi-cog"
			/>
			<AppTask
				v-if="mayDelete"
				:label="$t('quickTasks.delete.reader')"
				:confirm="$t('quickTasks.delete.reader.confirm')"
				icon="mdi-account dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteModel"
			/>
		</AppTasks>

		<SectionCard :loading="loading">
			<h1 class="text-h4">
				<LetterIcon :letter="letter" :color="reader.colour" />
				{{ reader.identifyingName }}
			</h1>
			<v-btn
				:to="`/readers/${reader.id}/favourites`"
				color="secondary" class="my-4 me-4" size="small" variant="outlined"
			>
				{{ $t('page.Reader.actions.favourites') }}
			</v-btn>
			<v-btn
				:to="`/readers/${reader.id}/readingList`"
				color="secondary" class="my-4" size="small" variant="outlined"
			>
				{{ $t('page.Reader.actions.readingList') }}
			</v-btn>
		</SectionCard>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleView } from '@/composables/model-view'
import readerService from '@/services/reader-service'

const mayDelete = ref(false)

async function fetchData(id: number): Promise<Reader> {
	const readerP = readerService.findById(id)
	mayDelete.value = await readerService.mayDeleteReader()
	return readerP
}

const { model: reader, appTasksMenu, loading, deleteModel } = useSimpleView(fetchData, readerService,
	'quickTasks.delete.reader.confirm.done', 'ReaderIndex')

const letter = computed(() => reader.value.name?.charAt(0))
</script>
