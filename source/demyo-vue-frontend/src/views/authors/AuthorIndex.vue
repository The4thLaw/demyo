<template>
	<div>
		<TextIndex :items="authors" :firstLetterExtractor="(item) => item.lname[0]">
			<template v-slot:default="slotProps">{{ slotProps.item.identifyingName }}</template>
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

	data() {
		return {
			authors: [
				{
					id: '1',
					fname: 'Jean-David',
					lname: 'Morvan',
					identifyingName: 'Jean-David Morvan'
				},
				{
					id: '2',
					fname: 'Jean-David 2',
					lname: 'Morvan 2',
					identifyingName: 'Jean-David 2 Morvan 2'
				}
			]
		}
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			let authors = await authorService.findForIndex()
			this.authors = authors
		}
	}
}
</script>