import publisherService from '@/services/publisher-service'

export default {
	data() {
		return {
			allPublishers: [],
			allPublishersLoading: false
		}
	},

	methods: {
		async refreshPublishers() {
			this.allPublishersLoading = true
			this.allPublishers = await publisherService.findForList()
			this.allPublishersLoading = false
		}
	}
}
