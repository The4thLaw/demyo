import { getParsedId } from '@/helpers/route'
import type AbstractModelService from '@/services/abstract-model-service'
import { useUiStore } from '@/stores/ui'
import { useHead } from '@unhead/vue'
import { useI18n } from 'vue-i18n'
import { useRoute, useRouter } from 'vue-router'

interface ViewData<T extends AbstractModel> {
	model: Ref<T>
	appTasksMenu: Ref<boolean>
	loading: Ref<boolean>
	loadData: () => Promise<void>
	deleteModel: () => Promise<void>
}

export function useSimpleView<T extends AbstractModel>(fetchData: (id: number) => Promise<T>,
		service: AbstractModelService<T>, confirmDeleteLabel: string | ((m: T) => string),
		indexRouteName: string,
		titleProvider = (m: T): string => m.identifyingName): ViewData<T> {
	const route = useRoute()
	const i18n = useI18n()
	const router = useRouter()
	const uiStore = useUiStore()

	const parsedId = ref(undefined) as Ref<number | undefined>
	const loading = ref(false)
	const model = ref({}) as Ref<T>
	const appTasksMenu = ref(false)

	useHead({
		title: () => titleProvider(model.value)
	})

	async function loadData(): Promise<void> {
		parsedId.value = getParsedId(route)
		loading.value = true
		model.value = await fetchData(parsedId.value)
		loading.value = false
	}

	watch(route, loadData)
	void loadData()

	async function deleteModel(): Promise<void> {
		appTasksMenu.value = false
		if (!parsedId.value) {
			return
		}

		const deleted = await service.deleteModel(parsedId.value)
		if (deleted) {
			if (typeof confirmDeleteLabel === 'function') {
				confirmDeleteLabel = confirmDeleteLabel(model.value)
			}
			uiStore.showSnackbar(i18n.t(confirmDeleteLabel))
			void router.push({ name: indexRouteName })
		}
	}

	return {
		model,
		appTasksMenu,
		loading,
		deleteModel,
		loadData
	}
}
