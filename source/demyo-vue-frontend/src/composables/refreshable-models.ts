import AbstractModelService from '@/services/abstract-model-service'
import imageService from '@/services/image-service'
import publisherService from '@/services/publisher-service'

function useRefreshable<M extends IModel>(service: AbstractModelService<M>) {
	const models = ref([] as M[])
	const loading = ref(false)

	async function load() {
		loading.value = true
		models.value = await service.findForList()
		loading.value = false
	}

	return {
		models,
		loading,
		load
	}
}

export function useRefreshableImages() {
	const refreshable = useRefreshable(imageService)
	return {
		images: refreshable.models,
		imagesLoading: refreshable.loading,
		loadImages: refreshable.load
	}
}

export function useRefreshablePublishers() {
	const refreshable = useRefreshable(publisherService)
	return {
		publishers: refreshable.models,
		publishersLoading: refreshable.loading,
		loadPublishers: refreshable.load
	}
}
