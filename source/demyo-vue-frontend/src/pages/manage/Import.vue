<template>
	<v-container>
		<SectionCard :title="$t('title.manage.import.select')">
			<v-file-input v-model="file" :label="$t('field.manage.import.file')" accept=".zip,.dea,.xml" />
		</SectionCard>

		<FormActions>
			<v-btn color="accent" @click="doImport">
				{{ $t('button.import') }}
			</v-btn>
		</FormActions>
	</v-container>
</template>

<script>
import FormActions from '@/components/FormActions'
import SectionCard from '@/components/SectionCard'
import service from '@/services/management-service'
import { useUiStore } from '@/stores/ui'

export default {
	name: 'Import',

	components: {
		FormActions,
		SectionCard
	},

	metaInfo() {
		return {
			title: this.$t('title.manage.import.select')
		}
	},

	data() {
		return {
			uiStore: useUiStore(),
			file: null
		}
	},

	computed: {
	},

	methods: {
		async doImport() {
			if (this.file) {
				this.uiStore.enableGlobalOverlay()
				const success = await service.doImport(this.file)
				this.uiStore.disableGlobalOverlay()
				if (success) {
					this.uiStore.showSnackbar(this.$t('page.Import.success'))
				} else {
					this.uiStore.showSnackbar(this.$t('core.exception.api.title'))
				}
				this.$router.push('/')
			}
		}
	}
}
</script>
