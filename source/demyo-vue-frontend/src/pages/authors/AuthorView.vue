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
				@confirm="deleteAuthor"
			/>
		</AppTasks>

		<SectionCard :loading="mainLoading" :image="author.portrait" :title="author.identifyingName">
			<FieldValue v-if="author.website" :label="$t('field.Author.website')">
				<a :href="author.website">{{ author.website }}</a>
			</FieldValue>
			<FieldValue v-if="author.birthDate" :label="$t('field.Author.birthDate')">
				{{ $d(new Date(author.birthDate), 'long') }}
				<span v-if="isAlive">({{ $t('field.Author.age.alive', { age }) }})</span>
			</FieldValue>
			<FieldValue v-if="author.deathDate" :label="$t('field.Author.deathDate')">
				{{ $d(new Date(author.deathDate), 'long') }}
				<span v-if="!isAlive">({{ $t('field.Author.age.dead', { age }) }})</span>
			</FieldValue>
			<FieldValue v-if="author.biography" :label="$t('field.Author.biography')">
				<!-- eslint-disable-next-line vue/no-v-html -->
				<div v-html="author.biography" />
			</FieldValue>
			<v-alert
				v-if="!albumsLoading && albums.length == 0"
				border="left" type="info" text class="my-4"
			>
				{{ $t('page.Author.noAlbums') }}
			</v-alert>
			<v-btn
				v-if="count > 0"
				:to="{ name: 'DerivativeIndex', query: { withArtist: author.id } }"
				color="accent" class="my-4" small outlined
			>
				{{ $tc('page.Author.viewDerivatives', count) }}
			</v-btn>
			<v-alert
				v-if="count === 0"
				border="left" type="info" text class="my-4"
			>
				{{ $t('page.Author.noDerivatives') }}
			</v-alert>
		</SectionCard>

		<SectionCard
			v-if="albumsLoading || albums.length > 0"
			:loading="albumsLoading"
			:title="$tc('page.Author.works', albumCount)"
		>
			<AlbumTextList :albums="albums">
				<template #default="slotProps">
					({{ describeAuthor(slotProps.album.id) }})
				</template>
			</AlbumTextList>
		</SectionCard>
	</v-container>
</template>

<script>
import AlbumTextList from '@/components/AlbumTextList.vue'
import AppTask from '@/components/AppTask.vue'
import AppTasks from '@/components/AppTasks.vue'
import FieldValue from '@/components/FieldValue.vue'
import SectionCard from '@/components/SectionCard.vue'
import { deleteStub } from '@/helpers/actions'
import modelViewMixin from '@/mixins/model-view'
import authorService from '@/services/author-service'
import dayjs from 'dayjs'

export default {
	name: 'AuthorView',

	components: {
		AlbumTextList,
		AppTask,
		AppTasks,
		FieldValue,
		SectionCard
	},

	mixins: [modelViewMixin],

	metaInfo() {
		return {
			title: this.author.identifyingName
		}
	},

	data() {
		return {
			mainLoading: true,
			albumsLoading: true,
			author: {},
			authorAlbums: {},
			count: -1,
			appTasksMenu: false
		}
	},

	computed: {
		albums() {
			return this.authorAlbums.albums || []
		},

		albumCount() {
			return this.albums.length
		},

		works() {
			return {
				asArtist: new Set(this.authorAlbums.asArtist),
				asWriter: new Set(this.authorAlbums.asWriter),
				asColorist: new Set(this.authorAlbums.asColorist),
				asInker: new Set(this.authorAlbums.asInker),
				asTranslator: new Set(this.authorAlbums.asTranslator)
			}
		},

		isAlive() {
			return this.author.birthDate && !this.author.deathDate
		},

		age() {
			if (!this.author.birthDate) {
				return null
			}
			const endDate = this.author.deathDate ? dayjs(this.author.deathDate) : dayjs()
			return endDate.diff(this.author.birthDate, 'year')
		}
	},

	methods: {
		async fetchData() {
			this.mainLoading = true
			const countP = authorService.countDerivatives(this.parsedId)
			this.author = await authorService.findById(this.parsedId)
			this.mainLoading = false

			this.authorAlbums = await authorService.getAuthorAlbums(this.parsedId)
			this.albumsLoading = false

			this.count = await countP
		},

		describeAuthor(albumId) {
			const qualifiers = []
			if (this.works.asArtist.has(albumId)) {
				qualifiers.push(this.$t('page.Author.works.role.artist'))
			}
			if (this.works.asWriter.has(albumId)) {
				qualifiers.push(this.$t('page.Author.works.role.writer'))
			}
			if (this.works.asColorist.has(albumId)) {
				qualifiers.push(this.$t('page.Author.works.role.colorist'))
			}
			if (this.works.asInker.has(albumId)) {
				qualifiers.push(this.$t('page.Author.works.role.inker'))
			}
			if (this.works.asTranslator.has(albumId)) {
				qualifiers.push(this.$t('page.Author.works.role.translator'))
			}
			return qualifiers.join(', ')
		},

		deleteAuthor() {
			deleteStub(this,
				() => authorService.deleteModel(this.author.id),
				'quickTasks.delete.author.confirm.done',
				'AuthorIndex')
		}
	}
}
</script>
