import { getParsedId } from '@/helpers/route'
import type AbstractModelService from '@/services/abstract-model-service'
import { useUiStore } from '@/stores/ui'
import { useHead } from '@unhead/vue'
import { useTemplateRef } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute, useRouter } from 'vue-router'

interface EditData<T extends AbstractModel> {
	model: Ref<Partial<T>>
	loading: Ref<boolean>
	save: () => Promise<number | undefined>
	reset: () => void
	loadData: () => Promise<void>
	formRef: Readonly<Ref<HTMLFormElement | null>>
}

// eslint-disable-next-line @typescript-eslint/max-params
function useEdit<T extends AbstractModel>(fetchData: (id: number | undefined) => Promise<Partial<T>>,
		service: AbstractModelService<T>, additionalLoaders: (() => Promise<unknown>)[],
		addTitleLabel?: string, editTitleLabel?: string, redirectRouteName?: string,
		saveHandler = async (m: T): Promise<number> => service.save(m)): EditData<T> {
	//
	const route = useRoute()
	const i18n = useI18n()
	const router = useRouter()
	const uiStore = useUiStore()
	const formRef = useTemplateRef<HTMLFormElement>('form')

	const parsedId = ref(undefined) as Ref<number | undefined>
	const loading = ref(false)
	const model = ref({}) as Ref<Partial<T>>

	if (addTitleLabel && editTitleLabel) {
		const pageTitle = computed(() => {
			if (loading.value) {
				return null
			}
			return parsedId.value ? i18n.t(editTitleLabel) : i18n.t(addTitleLabel)
		})

		useHead({
			title: pageTitle
		})
	}

	async function loadData(): Promise<void> {
		uiStore.enableGlobalOverlay()
		loading.value = true
		const loadPromises = [] as Promise<unknown>[]

		if (route.params.id) {
			parsedId.value = getParsedId(route)
		} else {
			parsedId.value = undefined
		}
		const modelP = fetchData(parsedId.value)
		loadPromises.push(modelP)
		// Set it as soon as it's resolved but without blocking other promises
		void modelP.then(m => (model.value = m))

		additionalLoaders.forEach(l => loadPromises.push(l()))

		await Promise.all(loadPromises)

		loading.value = false
		uiStore.disableGlobalOverlay()
	}

	uiStore.disableSearch()
	watch(route, loadData)
	void loadData()

	async function save(): Promise<number | undefined> {
		// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment, @typescript-eslint/no-unsafe-call
		const validation = await formRef.value?.validate()
		// eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
		if (!validation.valid) {
			console.debug('Form validation error', validation)
			return undefined
		}

		uiStore.enableGlobalOverlay()
		const id = await saveHandler(model.value as T)
		if (id <= 0) {
			uiStore.showSnackbar(i18n.t('core.exception.api.title'))
		} else if (redirectRouteName) {
			void router.push({ name: redirectRouteName, params: { id } })
		}
		uiStore.disableGlobalOverlay()

		return id
	}

	function reset(): void {
		if (formRef.value) {
			formRef.value.reset()
		}
		void loadData()
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

// eslint-disable-next-line @typescript-eslint/max-params
export function useSimpleEdit<T extends AbstractModel>(fetchData: (id: number | undefined) => Promise<Partial<T>>,
		service: AbstractModelService<T>, additionalLoaders: (() => Promise<unknown>)[],
		addTitleLabel: string, editTitleLabel: string, redirectRouteName: string,
		saveHandler = async (m: T): Promise<number> => service.save(m)): EditData<T> {
	return useEdit(fetchData, service, additionalLoaders, addTitleLabel, editTitleLabel, redirectRouteName, saveHandler)
}

export function useLightEdit<T extends AbstractModel>(fetchData: (id: number | undefined) => Promise<Partial<T>>,
		service: AbstractModelService<T>, additionalLoaders: (() => Promise<unknown>)[] = [],
		saveHandler = async (m: T): Promise<number> => service.save(m)): EditData<T> {
	return useEdit(fetchData, service, additionalLoaders, undefined, undefined, undefined, saveHandler)
}
