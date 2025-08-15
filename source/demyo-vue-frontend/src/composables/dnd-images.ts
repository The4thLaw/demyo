import { useUiStore } from '@/stores/ui'
import { useI18n } from 'vue-i18n'

interface DndHandlers {
	dndDialog: Ref<boolean>
	saveDndImages: (data: FilePondData) => Promise<void>
}

export function useDndImages(save: (data: FilePondData) => Promise<boolean>,
		refresh: () => Promise<void>): DndHandlers {
	const uiStore = useUiStore()
	const i18n = useI18n()

	const dndDialog = ref(false)

	async function saveDndImages(data: FilePondData): Promise<void> {
		const ok = await save(data)
		if (ok) {
			uiStore.showSnackbar(i18n.t('draganddrop.snack.confirm'))
			void refresh()
		} else {
			uiStore.showSnackbar(i18n.t('core.exception.api.title'))
		}
	}

	return {
		dndDialog,
		saveDndImages
	}
}
