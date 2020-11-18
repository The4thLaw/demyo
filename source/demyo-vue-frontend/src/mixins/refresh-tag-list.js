import tagService from '@/services/tag-service'

export default {
	data() {
		return {
			allTags: [],
			allTagsLoading: false
		}
	},

	methods: {
		async refreshTags() {
			this.allTagsLoading = true
			this.allTags = await tagService.findForList()
			this.allTagsLoading = false
		}
	}
}
