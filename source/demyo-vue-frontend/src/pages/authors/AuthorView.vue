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
			<FieldValue
				v-if="author.pseudonymOf?.id" :value="author.pseudonymOf"
				label-key="field.Author.pseudonymOf" type="AuthorView"
			/>
			<FieldValue :value="author.website" label-key="field.Author.website" type="url" />

			<FieldValue v-if="author.country" :value="author.country" label-key="field.Author.country">
				{{ country }}
			</FieldValue>

			<FieldValue :value="author.birthDate" label-key="field.Author.birthDate" type="date">
				<template v-if="isAlive" #append>
					({{ $t('field.Author.age.alive', { age }) }})
					<v-icon v-if="isBirthday" class="v-AuthorView__cake">
						mdi-cake-variant-outline
					</v-icon>
				</template>
			</FieldValue>

			<FieldValue :value="author.deathDate" label-key="field.Author.deathDate" type="date">
				<template v-if="!isAlive" #append>
					({{ $t('field.Author.age.dead', { age }) }})
				</template>
			</FieldValue>

			<FieldValue v-if="author.pseudonyms?.length" label-key="field.Author.pseudonyms">
				<ul>
					<li v-for="pseudo of author.pseudonyms" :key="pseudo.id">
						<ModelLink :model="pseudo" view="AuthorView" />
					</li>
				</ul>
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
					{{ describeAuthor(slotProps.album) }}
				</template>
			</AlbumTextList>
		</SectionCard>

		<SectionCard v-if="loading || genres.length > 0" :loading="loading" :title="$t('page.Author.genres')">
			<TaxonLink :model="genres" />
			<div class="v-AuthorView__genresChart">
				<Doughnut
					id="author-genres-chart"
					:data="chartData"
				/>
			</div>
		</SectionCard>
		Loading: {{ authorLoading }}
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleView } from '@/composables/model-view'
import { postProcessTaxons } from '@/composables/taxons'
import { useCountry } from '@/helpers/countries'
import authorService from '@/services/author-service'
import { ArcElement, Chart as ChartJS, Title, Tooltip } from 'chart.js'
import dayjs from 'dayjs'
import randomColor from 'randomcolor'
import { Doughnut } from 'vue-chartjs'
import { useI18n } from 'vue-i18n'

ChartJS.register(Title, Tooltip, ArcElement)

const authorLoading = ref(true)
const albumsLoading = ref(true)
const authorAlbums = ref({} as AuthorAlbums)
const derivativeCount = ref(-1)
const genres = ref([] as ProcessedTaxon[])

async function fetchData(id: number): Promise<Author> {
	authorLoading.value = true
	albumsLoading.value = true

	const derivCountP = authorService.countDerivatives(id)
	const genresP = authorService.getAuthorGenres(id)
	const authorP = await authorService.findById(id)
	authorLoading.value = false

	const loadedAuthorAlbums = await authorService.getAuthorAlbums(id)
	authorAlbums.value = JSON.parse(JSON.stringify(loadedAuthorAlbums)) as AuthorAlbums
	albumsLoading.value = false

	derivativeCount.value = await derivCountP

	const processedGenres = postProcessTaxons(await genresP)
	processedGenres.sort((g1, g2) => {
		const weightDiff = g2.relativeWeight - g1.relativeWeight
		if (weightDiff !== 0) {
			return weightDiff
		}
		return g1.identifyingName.toLowerCase().localeCompare(g2.identifyingName.toLowerCase())
	})
	genres.value = processedGenres

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
const isBirthday = computed(() => {
	if (!author.value.birthDate) {
		return false
	}
	const today = dayjs()
	const birth = dayjs(author.value.birthDate)
	return today.date() === birth.date() && today.month() === birth.month()
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

	let pseudonym = ''
	if (authorAlbums.value?.albumPseudonyms) {
		const albumPseudonym = authorAlbums.value?.albumPseudonyms[`${albumId}`]
		if (albumPseudonym && author.value.pseudonyms) {
			const pseudonymAuthor = author.value.pseudonyms.find(a => a.id === albumPseudonym)
			if (pseudonymAuthor) {
				pseudonym = `${i18n.t('field.Author.asPseudonym.before')} ${pseudonymAuthor.identifyingName
				}${i18n.t('field.Author.asPseudonym.after')}`
			}
		}
	}

	return `${qualifiers.join(', ')} ${pseudonym}`
}

const chartData = computed(() => {
	const labels = genres.value.map(g => g.identifyingName)
	const data = genres.value.map(g => g.usageCount)
	const backgroundColor = genres.value.map(g => g.bgColour ?? randomColor())
	return {
		labels,
		datasets: [{
			data,
			backgroundColor
		}]
	}
})
</script>

<style lang="scss">
.v-AuthorView__genresChart {
	width: max(200px, 20vw);
	margin: auto;
	position: relative;
}

.v-AuthorView__cake {
	margin-top: -0.3em;
}
</style>
