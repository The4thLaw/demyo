<template>
	<v-container>
		<AppTasks>
			<!--
				Consider making a generic icon overlay for those plus/minus icons.
			Interesting icons are: delete, account-minus, minus, plus, pencil, account-edit
				.v-icon { position: relative }
				.mdi-heart::after {

    content: 'a';
    content: "\F2D1";
    font: normal normal normal 12px/1 "Material Design Icons";
        font-size: 12px;
        line-height: 1;
    font-size: 24px;
    line-height: 1;
    font-size: inherit;
    text-rendering: auto;
    line-height: inherit;
    position: absolute;
    top: 0px;
    font-size: 12px;
    border: 1px solid

    red;

}
			-->
			<AppTask :label="$t('quickTasks.edit.author')" :to="`/authors/edit/${author.id}`" icon="mdi-pencil" />
			<AppTask :label="$t('quickTasks.delete.author')" icon="mdi-account-minus" @click="deleteAuthor" />
		</AppTasks>

		<SectionCard :loading="mainLoading" :image="author.portrait">
			<h1 class="display-1">{{ author.identifyingName }}</h1>
			<FieldValue :label="$t('field.Author.website')" :value="author.website">
				<a :href="author.website">{{ author.website }}</a>
			</FieldValue>
			<FieldValue :label="$t('field.Author.biography')" :value="author.biography">
				<div v-html="author.biography" />
			</FieldValue>
		</SectionCard>

		<SectionCard v-if="albumsLoading || albums.length > 0" :loading="albumsLoading">
			<h1 v-if="!albumsLoading" class="display-1">
				{{ $t('page.Author.works') }}
			</h1>
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
import { confirmAsyncAction } from '@/helpers/app-tasks-helpers'
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

	// TODO: title.author.view ?
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
			authorAlbums: {}
		}
	},

	computed: {
		basePortraitUrl() {
			let encodedName = encodeURI(this.author.portrait.userFileName)
			return '/images/' + this.author.portrait.id + '/file/' + encodedName
		},

		albums() {
			return this.authorAlbums.albums || []
		},

		works() {
			return {
				asArtist: new Set(this.authorAlbums.asArtist),
				asWriter: new Set(this.authorAlbums.asWriter),
				asColorist: new Set(this.authorAlbums.asColorist),
				asInker: new Set(this.authorAlbums.asInker),
				asTranslator: new Set(this.authorAlbums.asTranslator),
			}
		}
	},

	watch: {
		'$route': 'fetchData'
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.mainLoading = true
			const id = parseInt(this.$route.params.id, 10)

			this.author = await authorService.findById(id)
			this.mainLoading = false

			this.authorAlbums = await authorService.getAuthorAlbums(id)
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
			confirmAsyncAction(() => authorService.deleteAuthor(this.author.id),
				this.$t('quickTasks.delete.author.confirm'))
				.then(() => {
					// TODO: handle toast confirmation
					this.$router.push({ path: '/authors/' })
				})
		}
	}
}
</script>
