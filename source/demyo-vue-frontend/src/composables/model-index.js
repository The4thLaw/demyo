import { useUiStore } from '@/stores/ui'

/**
 * Composable for simple model indexes loading data from the service.
 * @param {AbstractModelService} serviceInstance The service to use to fetch the data.
 */
export function useSimpleIndex(serviceInstance) {
	const uiStore = useUiStore()

	const modelList = ref([])

	async function fetchData() {
		uiStore.enableGlobalOverlay()
		modelList.value = await serviceInstance.findForIndex()
		uiStore.disableGlobalOverlay()
	}

	fetchData()

	return { modelList }
}
