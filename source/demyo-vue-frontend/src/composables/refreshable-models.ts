import type AbstractModelService from '@/services/abstract-model-service'
import authorService from '@/services/author-service'
import bindingService from '@/services/binding-service'
import derivativeSourceService from '@/services/derivative-source-service'
import derivativeTypeService from '@/services/derivative-type-service'
import imageService from '@/services/image-service'
import publisherService from '@/services/publisher-service'
import seriesService from '@/services/series-service'
import tagService from '@/services/tag-service'

function useRefreshable<M extends IModel>(
		service: AbstractModelService<M>): { models: Ref<M[]>, loading: Ref<boolean>, load: () => Promise<void> } {
	const models = ref<M[]>([]) as Ref<M[]>
	const loading = ref(false)

	async function load(): Promise<void> {
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

interface RefreshAuthor { authors: Ref<Author[]>; authorsLoading: Ref<boolean>; loadAuthors: () => Promise<void> }
export function useRefreshableAuthors(): RefreshAuthor {
	const refreshable = useRefreshable(authorService)
	return {
		authors: refreshable.models,
		authorsLoading: refreshable.loading,
		loadAuthors: refreshable.load
	}
}

interface RefreshBinding { bindings: Ref<Binding[]>; bindingsLoading: Ref<boolean>; loadBindings: () => Promise<void> }
export function useRefreshableBindings(): RefreshBinding {
	const refreshable = useRefreshable(bindingService)
	return {
		bindings: refreshable.models,
		bindingsLoading: refreshable.loading,
		loadBindings: refreshable.load
	}
}

interface RefreshSource {
	sources: Ref<DerivativeSource[]>
	sourcesLoading: Ref<boolean>
	loadSources: () => Promise<void>
}
export function useRefreshableDerivativeSources(): RefreshSource {
	const refreshable = useRefreshable(derivativeSourceService)
	return {
		sources: refreshable.models,
		sourcesLoading: refreshable.loading,
		loadSources: refreshable.load
	}
}

interface RefreshDerivativeType {
	types: Ref<DerivativeType[]>
	typesLoading: Ref<boolean>
	loadTypes: () => Promise<void>
}
export function useRefreshableDerivativeTypes(): RefreshDerivativeType {
	const refreshable = useRefreshable(derivativeTypeService)
	return {
		types: refreshable.models,
		typesLoading: refreshable.loading,
		loadTypes: refreshable.load
	}
}

interface RefreshImage { images: Ref<Image[]>; imagesLoading: Ref<boolean>; loadImages: () => Promise<void> }
export function useRefreshableImages(): RefreshImage {
	const refreshable = useRefreshable(imageService)
	return {
		images: refreshable.models,
		imagesLoading: refreshable.loading,
		loadImages: refreshable.load
	}
}

interface RefreshPublisher {
	publishers: Ref<Publisher[]>
	publishersLoading: Ref<boolean>
	loadPublishers: () => Promise<void>
}
export function useRefreshablePublishers(): RefreshPublisher {
	const refreshable = useRefreshable(publisherService)
	return {
		publishers: refreshable.models,
		publishersLoading: refreshable.loading,
		loadPublishers: refreshable.load
	}
}

interface RefreshSeries { series: Ref<Series[]>; seriesLoading: Ref<boolean>; loadSeries: () => Promise<void> }
export function useRefreshableSeries(): RefreshSeries {
	const refreshable = useRefreshable(seriesService)
	return {
		series: refreshable.models,
		seriesLoading: refreshable.loading,
		loadSeries: refreshable.load
	}
}

interface RefreshTag { tags: Ref<Tag[]>; tagsLoading: Ref<boolean>; loadTags: () => Promise<void> }
export function useRefreshableTags(): RefreshTag {
	const refreshable = useRefreshable(tagService)
	return {
		tags: refreshable.models,
		tagsLoading: refreshable.loading,
		loadTags: refreshable.load
	}
}
