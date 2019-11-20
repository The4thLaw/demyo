<template>
	<v-container>
		<SectionCard :loading="mainLoading">
			<h1 class="display-1">{{ author.identifyingName }}</h1>
			<FieldValue :label="$t('field.Author.website')" :value="author.website">
				<a :href="author.website">{{ author.website }}</a>
			</FieldValue>
			<FieldValue :label="$t('field.Author.biography')" :value="author.biography">
				<div v-html="author.biography" />
			</FieldValue>
		</SectionCard>
	</v-container>
</template>

<script>
import FieldValue from '@/components/FieldValue'
import SectionCard from '@/components/SectionCard'
import authorService from '@/services/author-service'

export default {
	name: 'AuthorView',

	components: {
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
			this.mainLoading = true
			const id = parseInt(this.$route.params.id, 10)
			this.author = await authorService.findById(id)
			this.mainLoading = false
		}
	}
}
</script>

<style lang="less">

</style>
