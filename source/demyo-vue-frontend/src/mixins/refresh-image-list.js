import imageService from '@/services/image-service'

export default {
	data() {
		return {
			allImages: [],
			allImagesLoading: false
		}
	},

	methods: {
		async refreshImages() {
			this.allImagesLoading = true
			this.allImages = await imageService.findForList()
			this.allImagesLoading = false
		}
	}
}
