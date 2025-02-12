import AbstractModelService from '@/services/abstract-model-service'
import authorService from '@/services/author-service'
import bindingService from '@/services/binding-service'
import imageService from '@/services/image-service'
import publisherService from '@/services/publisher-service'
import seriesService from '@/services/series-service'
import tagService from '@/services/tag-service'

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

export function useRefreshableAuthors() {
	const refreshable = useRefreshable(authorService)
	return {
		authors: refreshable.models,
		authorsLoading: refreshable.loading,
		loadAuthors: refreshable.load
	}
}

export function useRefreshableBindings() {
	const refreshable = useRefreshable(bindingService)
	return {
		bindings: refreshable.models,
		bindingsLoading: refreshable.loading,
		loadBindings: refreshable.load
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

export function useRefreshableSeries() {
	const refreshable = useRefreshable(seriesService)
	return {
		series: refreshable.models,
		seriesLoading: refreshable.loading,
		loadSeries: refreshable.load
	}
}

export function useRefreshableTags() {
	const refreshable = useRefreshable(tagService)
	return {
		tags: refreshable.models,
		tagsLoading: refreshable.loading,
		loadTags: refreshable.load
	}
}
