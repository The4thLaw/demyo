<template>
	<div>
		<TextIndex :items="authors" :firstLetterExtractor="(item) => item.name[0]">
			<template v-slot:default="slotProps">
				<!-- TODO: link color -->
				<router-link :to="'/authors/view/' + slotProps.item.id">
					{{ slotProps.item.identifyingName }}
				</router-link>
			</template>
		</TextIndex>
	</div>
</template>

<script>
import TextIndex from '@/components/TextIndex'
import authorService from '@/services/author-service'

export default {
	name: 'AuthorIndex',

	components: {
		TextIndex
	},

	metaInfo() {
		return {
			title: this.$t('title.index.author')
		}
	},

	data() {
		return {
			authors: []
		}
	},

	created() {
		this.fetchData()
		// TODO: left here for demo purposes, remove soon
		this.$store.dispatch('ui/disableSearch')
	},

	methods: {
		async fetchData() {
			let authors = await authorService.findForIndex()
			this.authors = authors
		}
	}
}
</script>
