import type AbstractModelService from '@/services/abstract-model-service'
import { useUiStore } from '@/stores/ui'
import { useHead } from '@unhead/vue'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

interface Index<T extends IModel> {
	loading: Ref<boolean>
	modelList: Ref<T[]>
}

/**
 * Composable for simple model indexes loading data from the service.
 * @param serviceInstance The service to use to fetch the data.
 * @param titleKey the page title key
 */
export function useSimpleIndex<T extends IModel>(serviceInstance: AbstractModelService<T>, titleKey: string,
		fetchData?: (() => Promise<T[]>)): Index<T> {
	const safeFetchData: () => Promise<T[]> = fetchData ?? (async ():Promise<T[]> => serviceInstance.findForIndex())

	useHead({
		title: computed(() => useI18n().t(titleKey))
	})

	const uiStore = useUiStore()
	const loading = computed(() => uiStore.globalOverlay)

	const modelList = ref([] as T[]) as Ref<T[]>

	async function loadData(): Promise<void> {
		uiStore.enableGlobalOverlay()
		modelList.value = await safeFetchData()
		uiStore.disableGlobalOverlay()
	}

	void loadData()

	return { loading, modelList }
}
