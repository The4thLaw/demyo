import { getParsedId } from "@/helpers/route"
import AbstractModelService from "@/services/abstract-model-service"
import { useUiStore } from "@/stores/ui"
import { useHead } from "@unhead/vue"
import { useI18n } from "vue-i18n"
import { useRoute, useRouter } from "vue-router"

interface ViewData<T extends AbstractModel> {
	model: Ref<T>
	appTasksMenu: Ref<boolean>
	loading: Ref<boolean>
	loadData: () => void
	deleteModel: () => void
}

export function useSimpleView<T extends AbstractModel>(fetchData: (id: number) => Promise<T>,
	service: AbstractModelService<T>, confirmDeleteLabel: string, indexRouteName: string,
	titleProvider = (model: T) => model.identifyingName): ViewData<T> {
	//
	const route = useRoute()
	const i18n = useI18n()
	const router = useRouter()
	const uiStore = useUiStore()

	const parsedId = ref(undefined) as Ref<number | undefined>
	const loading = ref(false)
	const model = ref({}) as Ref<T>
	const appTasksMenu = ref(false)

	async function loadData() {
		parsedId.value = getParsedId(route)
		loading.value = true
		model.value = await fetchData(parsedId.value)
		useHead({
			title: titleProvider(model.value)
		})
		loading.value = false
	}

	watch(route, loadData)
	loadData()

	async function deleteModel() {
		appTasksMenu.value = false
		if (!parsedId.value) {
			return
		}

		const deleted = await service.deleteModel(parsedId.value)
		if (deleted) {
			uiStore.showSnackbar(i18n.t(confirmDeleteLabel))
			router.push({ name: indexRouteName })
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
