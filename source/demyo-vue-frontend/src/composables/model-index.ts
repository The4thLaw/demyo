import type AbstractModelService from '@/services/abstract-model-service'
import { useUiStore } from '@/stores/ui'
import { useHead } from '@unhead/vue'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

/**
 * Composable for simple model indexes loading data from the service.
 * @param serviceInstance The service to use to fetch the data.
 * @param titleKey the page title key
 */
export function useSimpleIndex<T extends IModel>(serviceInstance: AbstractModelService<T>, titleKey: string,
	fetchData?: (() => Promise<T[]>)) {
	//
	let safeFetchData: () => Promise<T[]>
	safeFetchData = fetchData || (async () => serviceInstance.findForIndex())

	useHead({
		title: useI18n().t(titleKey)
	})

	const uiStore = useUiStore()
	const loading = computed(() => uiStore.globalOverlay)

	const modelList = ref([] as T[])

	async function loadData() {
		uiStore.enableGlobalOverlay()
		modelList.value = await safeFetchData()
		uiStore.disableGlobalOverlay()
	}

	loadData()

	return { loading, modelList }
}
