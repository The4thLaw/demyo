<template>
	<v-container>
		<!-- TODO: check if 8 units is the right way to pad, or if 4 should be used -->
		<v-card class="pa-8">
			<h1 class="display-1">{{ author.identifyingName }}</h1>
			<!-- TODO: extract to a FieldValue component with a title prop and a slot for the content. Include the v-if by default -->
			<div>
				<div><strong>{{ $t('field.Author.website') }}</strong></div>
				<div>{{ author.website }}</div>
			</div>
			<div v-if="author.biography">
				<div><strong>{{ $t('field.Author.biography') }}</strong></div>
				<div>{{ author.biography }}</div>
			</div>
		</v-card>
	</v-container>
</template>

<script>
import authorService from '@/services/author-service'

export default {
	name: 'AuthorView',

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
