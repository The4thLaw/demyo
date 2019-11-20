<template>
	<v-container>
		<v-card class="pa-6">
			<h1 class="display-1">{{ author.identifyingName }}</h1>
			<!-- TODO: extract to a FieldValue component with a title prop and a slot for the content. Include the v-if by default -->
			<FieldValue :label="$t('field.Author.website')" :value="author.website">
				<a :href="author.website">{{ author.website }}</a>
			</FieldValue>
			<FieldValue :label="$t('field.Author.biography')" :value="author.biography">
				<div v-html="author.biography" />
			</FieldValue>
		</v-card>
	</v-container>
</template>

<script>
import FieldValue from '@/components/FieldValue'
import authorService from '@/services/author-service'

export default {
	name: 'AuthorView',

	components: {
		FieldValue
	},

	// TODO: title.author.view ?
	metaInfo() {
		return {
			title: this.author.identifyingName
		}
	},

	data() {
		return {
			author: {}
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
			const id = parseInt(this.$route.params.id, 10)
			this.author = await authorService.findById(id)
		}
	}
}
</script>

<style lang="less">

</style>
