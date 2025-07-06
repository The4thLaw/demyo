<template>
	<v-container>
		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.author')" :to="`/authors/${author.id}/edit`"
				icon="mdi-account dem-overlay-edit"
			/>
			<AppTask
				:label="$t('quickTasks.delete.author')"
				:confirm="$t('quickTasks.delete.author.confirm')"
				icon="mdi-account dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteModel"
			/>
		</AppTasks>

		<SectionCard :loading="authorLoading" :image="author.portrait" :title="author.identifyingName">
			<FieldValue :value="author.website" label-key="field.Author.website" type="url" />

			<FieldValue v-if="author.country" :value="author.country" label-key="field.Author.country">
				{{ country }}
			</FieldValue>

			<FieldValue :value="author.birthDate" label-key="field.Author.birthDate" type="date">
				<template v-if="isAlive" #append>
					({{ $t('field.Author.age.alive', { age }) }})
				</template>
			</FieldValue>

			<FieldValue :value="author.deathDate" label-key="field.Author.deathDate" type="date">
				<template v-if="!isAlive" #append>
					({{ $t('field.Author.age.dead', { age }) }})
				</template>
			</FieldValue>

			<FieldValue :value="author.biography" label-key="field.Author.biography" type="rich-text" />
			<v-alert
				v-if="!albumsLoading && albums.length === 0"
				border="start" type="info" text class="my-4"
				variant="outlined"
			>
				{{ $t('page.Author.noAlbums') }}
			</v-alert>
			<v-btn
				v-if="derivativeCount > 0"
				:to="{ name: 'DerivativeIndex', query: { withArtist: author.id } }"
				color="secondary" class="my-4" size="small" variant="outlined"
			>
				{{ $t('page.Author.viewDerivatives', derivativeCount) }}
			</v-btn>
			<v-alert
				v-if="derivativeCount === 0"
				border="start" type="info" text class="my-4"
				variant="outlined"
			>
				{{ $t('page.Author.noDerivatives') }}
			</v-alert>
		</SectionCard>

		<SectionCard
			v-if="albumsLoading || albums.length > 0"
			:loading="albumsLoading"
			:title="$t('page.Author.works', albumCount)"
		>
			<AlbumTextList :albums="albums">
				<template #default="slotProps">
					({{ describeAuthor(slotProps.album) }})
				</template>
			</AlbumTextList>
		</SectionCard>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleView } from '@/composables/model-view'
import { useCountry } from '@/helpers/countries'
import authorService from '@/services/author-service'
import dayjs from 'dayjs'
import { useI18n } from 'vue-i18n'

const authorLoading = ref(true)
const albumsLoading = ref(true)
const authorAlbums = ref({} as AuthorAlbums)
const derivativeCount = ref(-1)

async function fetchData(id: number): Promise<Author> {
	authorLoading.value = true
	albumsLoading.value = true

	const derivCountP = authorService.countDerivatives(id)
	const authorP = await authorService.findById(id)
	authorLoading.value = false

	const loadedAuthorAlbums = await authorService.getAuthorAlbums(id)
	authorAlbums.value = JSON.parse(JSON.stringify(loadedAuthorAlbums)) as AuthorAlbums
	albumsLoading.value = false

	derivativeCount.value = await derivCountP

	return authorP
}

const { model: author, loading, appTasksMenu, deleteModel } = useSimpleView(fetchData, authorService,
	'quickTasks.delete.author.confirm.done', 'AuthorIndex')

const albums = computed(() => authorAlbums.value.albums || [])
const albumCount = computed(() => albums.value.length)
const works = computed(() => ({
	asArtist: new Set(authorAlbums.value.asArtist),
	asWriter: new Set(authorAlbums.value.asWriter),
	asColorist: new Set(authorAlbums.value.asColorist),
	asInker: new Set(authorAlbums.value.asInker),
	asTranslator: new Set(authorAlbums.value.asTranslator),
	asCoverArtist: new Set(authorAlbums.value.asCoverArtist)
}))
const isAlive = computed(() => author.value.birthDate && !author.value.deathDate)
const age = computed(() => {
	if (!author.value.birthDate) {
		return null
	}
	const endDate = author.value.deathDate ? dayjs(author.value.deathDate) : dayjs()
	return endDate.diff(author.value.birthDate, 'year')
})
const country = useCountry(computed(() => author.value.country))

const i18n = useI18n()
function describeAuthor(album: Album): string {
	const albumId = album.id
	const qualifiers = []
	if (works.value.asArtist.has(albumId)) {
		qualifiers.push(i18n.t(`page.Author.works.role.artist.${album.bookType.labelType}`))
	}
	if (works.value.asWriter.has(albumId)) {
		qualifiers.push(i18n.t(`page.Author.works.role.writer.${album.bookType.labelType}`))
	}
	if (works.value.asColorist.has(albumId)) {
		qualifiers.push(i18n.t('page.Author.works.role.colorist'))
	}
	if (works.value.asInker.has(albumId)) {
		qualifiers.push(i18n.t('page.Author.works.role.inker'))
	}
	if (works.value.asTranslator.has(albumId)) {
		qualifiers.push(i18n.t('page.Author.works.role.translator'))
	}
	if (works.value.asCoverArtist.has(albumId)) {
		qualifiers.push(i18n.t('page.Author.works.role.coverArtist'))
	}
	return qualifiers.join(', ')
}
</script>
