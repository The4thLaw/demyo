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
import { apiRoot } from '@/myenv'
import FormActions from '@/components/FormActions'
import SectionCard from '@/components/SectionCard'
import service from '@/services/management-service'

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
			file: null
		}
	},

	methods: {
		async doImport() {
			if (this.file) {
				this.$store.dispatch('ui/enableGlobalOverlay')
				let success = await service.doImport(this.file)
				this.$store.dispatch('ui/disableGlobalOverlay')
				if (success) {
					this.$store.dispatch('ui/showSnackbar', this.$t('page.Import.success'))
				} else {
					this.$store.dispatch('ui/showSnackbar', this.$t('core.exception.api.title'))
				}
				this.$router.push('/')
			}
		}
	}
}
</script>
