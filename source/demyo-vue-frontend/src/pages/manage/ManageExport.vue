<template>
	<v-container>
		<SectionCard :title="$t('title.manage.export.select')">
			<v-select v-model="format" :label="$t('field.manage.export.format')" :items="formats" />
			<v-checkbox v-model="withResources" :label="$t('field.manage.export.withResources.edit')" />
		</SectionCard>
		<FormActions>
			<v-btn color="secondary" @click="doExport">
				{{ $t('button.export') }}
			</v-btn>
		</FormActions>
		<!-- This is the form that will be posted -->
		<form ref="exportForm" method="get" :action="postUrl">
			<input v-model="format" type="hidden" name="format">
			<input v-model="withResources" type="hidden" name="withResources">
		</form>
	</v-container>
</template>

<script>
import { apiRoot } from '@/myenv'

export default {
	name: 'ManageExport',

	data() {
		return {
			format: 'XML',
			formats: [
				{
					title: 'Demyo 2',
					value: 'XML'
				}
			],
			withResources: true
		}
	},

	head() {
		return {
			title: this.$t('title.manage.export.select')
		}
	},

	computed: {
		postUrl() {
			return apiRoot + 'manage/export'
		}
	},

	methods: {
		doExport() {
			this.$refs.exportForm.submit()
		}
	}
}
</script>
