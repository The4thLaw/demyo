<template>
	<v-container>
		<SectionCard :title="$t('title.manage.export.select')">
			<v-select v-model="format" :label="$t('field.manage.export.format')" :items="formats" />
			<v-checkbox v-model="withResources" :label="$t('field.manage.export.withResources.edit')" />
		</SectionCard>
		<FormActions>
			<v-btn color="accent" @click="doExport">
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
import FormActions from '@/components/FormActions.vue'
import SectionCard from '@/components/SectionCard.vue'
import { apiRoot } from '@/myenv'

export default {
	name: 'Export',

	components: {
		FormActions,
		SectionCard
	},

	metaInfo() {
		return {
			title: this.$t('title.manage.export.select')
		}
	},

	data() {
		return {
			format: 'XML',
			formats: [
				{
					text: 'Demyo 2',
					value: 'XML'
				}
			],
			withResources: true
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
