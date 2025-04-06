<template>
	<v-container>
		<SectionCard :title="$t('title.manage.import.select')">
			<v-file-input v-model="file" :label="$t('field.manage.import.file')" accept=".zip,.dea,.xml" />
		</SectionCard>

		<FormActions>
			<v-btn color="secondary" @click="doImport">
				{{ $t('button.import') }}
			</v-btn>
		</FormActions>
	</v-container>
</template>

<script setup lang="ts">
import service from '@/services/management-service'
import { useUiStore } from '@/stores/ui'
import { useHead } from '@unhead/vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'

const router = useRouter()
const uiStore = useUiStore()
const i18n = useI18n()

useHead({
	title: i18n.t('title.manage.import.select')
})

const file: Ref<File | undefined> = ref(undefined)

async function doImport(): Promise<void> {
	if (file.value) {
		uiStore.enableGlobalOverlay()
		const success = await service.doImport(file.value)
		uiStore.disableGlobalOverlay()
		if (success) {
			uiStore.showSnackbar(i18n.t('page.Import.success'))
		} else {
			uiStore.showSnackbar(i18n.t('core.exception.api.title'))
		}
		void router.push('/')
	}
}
</script>
