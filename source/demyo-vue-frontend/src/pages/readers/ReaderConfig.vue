<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :loading="loading" :title="$t('page.Configuration.reader', { reader: reader.name })">
				<v-select
					v-model="reader.configuration.language" :label="$t('field.config.reader.language')"
					:items="languages"
				/>
				<v-text-field
					v-model="reader.configuration.pageSizeForText"
					:label="$t('field.config.reader.pageSizeForText')"
					type="number" inputmode="decimal" step="any"
				/>
				<v-text-field
					v-model="reader.configuration.pageSizeForCards"
					:label="$t('field.config.reader.pageSizeForCards')"
					type="number" inputmode="decimal" step="any"
				/>
				<v-text-field
					v-model="reader.configuration.subItemsInCardIndex"
					:label="$t('field.config.reader.subItemsInCardIndex')"
					type="number" inputmode="decimal" step="any"
				/>
				<v-text-field
					v-model="reader.configuration.pageSizeForImages"
					:label="$t('field.config.reader.pageSizeForImages')"
					type="number" inputmode="decimal" step="any"
				/>
			</SectionCard>

			<SectionCard :loading="loading" :title="$t('page.Configuration.global')">
				<p v-text="$t('page.Configuration.global.description')" />
				<v-autocomplete
					v-model="reader.configuration.currency" :label="$t('field.config.global.currency')"
					:items="currencies"
				/>
			</SectionCard>

			<FormActions v-if="!loading" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleEdit } from '@/composables/model-edit'
import { currencyList as currencies } from '@/helpers/i18n'
import readerService from '@/services/reader-service'
import { useI18n } from 'vue-i18n'

interface OptionValue {
	title: string,
	value: string
}

const i18n = useI18n()

const languages = ref([] as OptionValue[])

async function fetchData(id: number | undefined): Promise<Partial<Reader>> {
	if (!id) {
		throw new Error('Cannot load the configuration for an undefined reader')
	}
	return readerService.findById(id)
		.then(fetched => {
			// Must load language name translations *after* the page is initialized
			// Before, we would just should the label codes
			const languageCodes = ['en', 'fr']
			languages.value = languageCodes.map(c => {
				return {
					value: c,
					title: i18n.t(`core.language.${c}`)
				}
			})
			return fetched
		})
}

const { model: reader, loading, save, reset } = useSimpleEdit(fetchData, readerService, [],
	'menu.reader.configuration', 'menu.reader.configuration', 'ReaderView')
</script>
