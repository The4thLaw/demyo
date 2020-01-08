import authorService from '@/services/author-service'

export default {
	data() {
		return {
			allAuthors: [],
			allAuthorsLoading: false
		}
	},

	methods: {
		async refreshAuthors() {
			this.allAuthorsLoading = true
			this.allAuthors = await authorService.findForList()
			this.allAuthorsLoading = false
		}
	}
}
