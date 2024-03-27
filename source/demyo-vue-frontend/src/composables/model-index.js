import { useUiStore } from '@/stores/ui'
import { useHead } from '@unhead/vue'
import { useI18n } from 'vue-i18n'

/**
 * Composable for simple model indexes loading data from the service.
 * @param {AbstractModelService} serviceInstance The service to use to fetch the data.
 * @param {string} titleKey the page title key
 */
export function useSimpleIndex(serviceInstance, titleKey) {
	useHead({
		title: useI18n().t(titleKey)
	})

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
