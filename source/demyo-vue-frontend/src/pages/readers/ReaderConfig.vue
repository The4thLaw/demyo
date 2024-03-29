<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :loading="!initialized" :title="$t('page.Configuration.reader', { reader: reader.name })">
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

			<SectionCard :loading="!initialized" :title="$t('page.Configuration.global')">
				<p v-text="$t('page.Configuration.global.description')" />
				<v-autocomplete
					v-model="reader.configuration.currency" :label="$t('field.config.global.currency')"
					:items="currencies"
				/>
			</SectionCard>

			<FormActions v-if="initialized" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script>
import FormActions from '@/components/FormActions.vue'
import SectionCard from '@/components/SectionCard.vue'
import { currencyList } from '@/helpers/i18n'
import { mandatory } from '@/helpers/rules'
import modelEditMixin from '@/mixins/model-edit'
import readerService from '@/services/reader-service'

export default {
	name: 'ReaderConfig',

	components: {
		FormActions,
		SectionCard
	},

	mixins: [modelEditMixin],

	data() {
		return {
			mixinConfig: {
				modelEdit: {
					titleKeys: {
						add: 'menu.reader.configuration',
						edit: 'menu.reader.configuration'
					},
					saveRedirectViewName: 'ReaderView'
				}
			},

			reader: {
				configuration: {}
			},

			languages: [],
			currencies: currencyList,

			rules: {
				name: [
					mandatory()
				]
			}
		}
	},
	methods: {
		async fetchData() {
			this.reader = await readerService.findById(this.parsedId)

			// Must load language name translations *after* the page is initialized
			// Before, we would just should the label codes
			const languageCodes = ['en', 'fr']
			this.languages = languageCodes.map(c => {
				return {
					value: c,
					text: this.$t(`core.language.${c}`)
				}
			})
		},

		saveHandler() {
			return readerService.save(this.reader)
		}
	}
}
</script>
