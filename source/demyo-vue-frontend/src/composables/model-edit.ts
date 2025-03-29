import { getParsedId } from "@/helpers/route"
import AbstractModelService from "@/services/abstract-model-service"
import { useUiStore } from "@/stores/ui"
import { useHead } from "@unhead/vue"
import { useTemplateRef } from "vue"
import { useI18n } from "vue-i18n"
import { useRoute, useRouter } from "vue-router"

interface EditData<T extends AbstractModel> {
	model: Ref<Partial<T>>
	loading: Ref<boolean>
	save: () => void
	reset: () => void
	loadData: () => void
	formRef: Readonly<Ref<HTMLFormElement | null>>
}


export function useSimpleEdit<T extends AbstractModel>(fetchData: (id: number|undefined) => Promise<Partial<T>>,
	service: AbstractModelService<T>, additionalLoaders: (() => Promise<any>)[],
	addTitleLabel: string, editTitleLabel: string, redirectRouteName: string,
	saveHandler = (model: T) => service.save(model)): EditData<T> {
	//
	const route = useRoute()
	const i18n = useI18n()
	const router = useRouter()
	const uiStore = useUiStore()
	const formRef = useTemplateRef<HTMLFormElement, string>('form')

	const parsedId = ref(undefined) as Ref<number | undefined>
	const loading = ref(false)
	const model = ref({}) as Ref<Partial<T>>

	const pageTitle = computed(() => {
		if (loading.value) {
			return null
		}
		return parsedId.value ? i18n.t(editTitleLabel) : i18n.t(addTitleLabel)
	})

	async function loadData() {
		useHead({
			title: pageTitle
		})

		uiStore.enableGlobalOverlay()
		loading.value = true
		const loadPromises = [] as Promise<any>[]

		if (route.params.id) {
			parsedId.value = getParsedId(route)
		} else {
			parsedId.value = undefined
		}
		const modelP = fetchData(parsedId.value)
		loadPromises.push(modelP)
		// Set it as soon as it's resolved but without blocking other promises
		modelP.then(m => model.value = m)

		additionalLoaders.forEach(l => loadPromises.push(l()))

		await Promise.all(loadPromises)

		loading.value = false
		uiStore.disableGlobalOverlay()
	}

	uiStore.disableSearch()
	watch(route, loadData)
	loadData()

	async function save() {
		const validation = await formRef.value?.validate()
		if (!validation.valid) {
			console.debug('Form validation error', validation)
			return
		}

		uiStore.enableGlobalOverlay()
		const id = await saveHandler(model.value as T)
		if (id <= 0) {
			uiStore.showSnackbar(i18n.t('core.exception.api.title'))
		} else {
			router.push({ name: redirectRouteName, params: { id: id }})
		}
		uiStore.disableGlobalOverlay()
	}

	function reset() {
		if (formRef.value) {
			formRef.value.reset()
		}
		loadData()
	}

	return {
		model,
		loading,
		save,
		reset,
		loadData,
		formRef
	}
}
