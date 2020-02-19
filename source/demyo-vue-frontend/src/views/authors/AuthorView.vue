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
			<FieldValue :label="$t('field.Author.website')" :value="author.website">
				<a :href="author.website">{{ author.website }}</a>
			</FieldValue>
			<FieldValue :label="$t('field.Author.biography')" :value="author.biography">
				<div v-html="author.biography" />
			</FieldValue>
		</SectionCard>

		<SectionCard
			v-if="albumsLoading || albums.length > 0"
			:loading="albumsLoading"
			:title="$t('page.Author.works')"
		>
			<AlbumTextList :albums="albums">
				<template v-slot:default="slotProps">
					({{ describeAuthor(slotProps.album.id) }})
				</template>
			</AlbumTextList>
		</SectionCard>
	</v-container>
</template>

<script>
import AlbumTextList from '@/components/AlbumTextList'
import AppTask from '@/components/AppTask'
import AppTasks from '@/components/AppTasks'
import FieldValue from '@/components/FieldValue'
import SectionCard from '@/components/SectionCard'
import { deleteStub } from '@/helpers/actions'
import modelViewMixin from '@/mixins/model-view'
import authorService from '@/services/author-service'

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
			appTasksMenu: false
		}
	},

	computed: {
		albums() {
			return this.authorAlbums.albums || []
		},

		works() {
			return {
				asArtist: new Set(this.authorAlbums.asArtist),
				asWriter: new Set(this.authorAlbums.asWriter),
				asColorist: new Set(this.authorAlbums.asColorist),
				asInker: new Set(this.authorAlbums.asInker),
				asTranslator: new Set(this.authorAlbums.asTranslator)
			}
		}
	},

	methods: {
		async fetchData() {
			this.mainLoading = true
			this.author = await authorService.findById(this.parsedId)
			this.mainLoading = false

			this.authorAlbums = await authorService.getAuthorAlbums(this.parsedId)
			this.albumsLoading = false
		},

		describeAuthor(albumId) {
			let qualifiers = []
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
