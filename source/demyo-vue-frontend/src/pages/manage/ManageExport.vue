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
		<form ref="export-form" method="get" :action="postUrl">
			<input v-model="format" type="hidden" name="format">
			<input v-model="withResources" type="hidden" name="withResources">
		</form>
	</v-container>
</template>

<script setup lang="ts">
import { apiRoot } from '@/myenv'
import { useHead } from '@unhead/vue'
import { useTemplateRef } from 'vue'
import { useI18n } from 'vue-i18n'

const formats = [
	{
		title: 'Demyo 2',
		value: 'XML'
	}
]
const postUrl = `${apiRoot}manage/export`

const format = ref('XML')
const withResources = ref(true)
const exportForm = useTemplateRef<HTMLFormElement>('export-form')

useHead({
	title: useI18n().t('title.manage.export.select')
})

function doExport() {
	exportForm.value?.submit()
}
</script>
