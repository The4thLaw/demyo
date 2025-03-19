<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.tag')"
				:to="`/tags/${tag.id}/edit`"
				icon="mdi-tag dem-overlay-edit"
			/>
			<!-- Note that we can always delete tags -->
			<AppTask
				:label="$t('quickTasks.delete.tag')"
				:confirm="$t('quickTasks.delete.tag.confirm')"
				icon="mdi-tag dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteModel"
			/>
		</AppTasks>

		<SectionCard :loading="loading">
			<h1 v-if="!loading" class="text-h4">
				<span class="d-Tag" :style="style">
					{{ tag.identifyingName }}
				</span>
			</h1>
			<FieldValue v-if="tag.description" :label="$t('field.Tag.description')">
				<!-- eslint-disable-next-line vue/no-v-html -->
				<div v-html="tag.description" />
			</FieldValue>
			<v-btn
				v-if="albumCount > 0"
				:to="{ name: 'AlbumIndex', query: { withTag: tag.id } }"
				color="secondary" class="my-4" size="small" variant="outlined"
			>
				{{ $t('page.Tag.viewAlbums', albumCount) }}
			</v-btn>
			<v-alert
				v-if="albumCount === 0"
				border="start" type="info" text class="my-4"
			>
				{{ $t('page.Tag.noAlbums') }}
			</v-alert>
		</SectionCard>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleView } from '@/composables/model-view'
import { useTagStyle } from '@/composables/tags'
import tagService from '@/services/tag-service'

const albumCount = ref(-1)

async function fetchData(id: number): Promise<Tag> {
	const tagP = tagService.findById(id)
	albumCount.value = await tagService.countAlbums(id)
	return tagP
}

const { model: tag, appTasksMenu, loading, deleteModel } = useSimpleView(fetchData, tagService,
	'quickTasks.delete.tag.confirm.done', 'TagIndex')

const style = useTagStyle(tag)
</script>
