<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.bookType')"
				:to="`/bookTypes/${bookType.id}/edit`"
				icon="mdi-bookshelf dem-overlay-edit"
			/>
			<AppTask
				v-if="typeCount > 1 && albumCount === 0"
				:label="$t('quickTasks.delete.bookType')"
				:confirm="$t('quickTasks.delete.bookType.confirm')"
				icon="mdi-bookshelf dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteModel"
			/>
		</AppTasks>

		<SectionCard :loading="loading" :title="bookType.identifyingName">
			<FieldValue v-if="bookType.description" :label="$t('field.BookType.description')">
				<!-- eslint-disable-next-line vue/no-v-html -->
				<div v-html="bookType.description" />
			</FieldValue>

			<FieldValue :label="$t('field.BookType.labelType')">
				{{ $t(`field.BookType.labelType.value.${bookType.labelType}`) }}
			</FieldValue>

			<FieldValue v-if="bookType.structuredFieldConfig?.length" :label="$t('field.BookType.fieldConfig')">
				<ul>
					<li v-if="fieldConfig.has('ALBUM_ARTIST')">
						{{ $t(`field.Album.artists.${bookType.labelType}`) }}
					</li>
					<li v-if="fieldConfig.has('ALBUM_COLORIST')">
						{{ $t('field.Album.colorists') }}
					</li>
					<li v-if="fieldConfig.has('ALBUM_INKER')">
						{{ $t('field.Album.inkers') }}
					</li>
					<li v-if="fieldConfig.has('ALBUM_TRANSLATOR')">
						{{ $t('field.Album.translators') }}
					</li>
				</ul>
			</FieldValue>

			<v-alert
				border="start" type="info" text class="my-4"
			>
				<template v-if="albumCount === 0">
					{{ $t('page.BookType.noAlbums') }}
				</template>
				<template v-else>
					{{ $t('page.BookType.albumCount', albumCount) }}
				</template>
			</v-alert>
		</SectionCard>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleView } from '@/composables/model-view'
import bookTypeService from '@/services/book-type-service'

const typeCount = ref(-1)
const albumCount = ref(-1)

async function fetchData(id: number): Promise<BookType> {
	const bookTypeP = bookTypeService.findById(id)
	typeCount.value = await bookTypeService.count()
	albumCount.value = await bookTypeService.countAlbums(id)
	return bookTypeP // Resolve calls in parallel
}

const { model: bookType, loading, appTasksMenu, deleteModel }
	= useSimpleView(fetchData, bookTypeService, 'quickTasks.delete.bookType.confirm.done', 'TypeManagement')

const fieldConfig = computed(() => new Set(bookType.value.structuredFieldConfig ?? []))
</script>
