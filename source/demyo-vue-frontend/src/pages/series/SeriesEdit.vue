<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Series.identification')">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="series.name" :label="$t('field.Series.name')" :rules="rules.name" required
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="series.originalName" :label="$t('field.Series.originalName')"
						/>
					</v-col>
					<v-col cols="12">
						<Autocomplete
							v-model="series.relatedSeries" :items="allSeries" multiple
							label-key="field.Series.relatedSeries"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-checkbox v-model="series.completed" :label="$t('field.Series.completed.edit')" />
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field v-model="series.website" :label="$t('field.Series.website')" />
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field v-model="series.location" :label="$t('field.Series.location')" />
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Series.freeText')">
				<v-row>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">{{ $t('field.Series.summary') }}</label>
						<tiptap-vuetify
							v-model="series.summary" :extensions="tipTapExtensions"
							:card-props="{ outlined: true }"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">{{ $t('field.Series.comment') }}</label>
						<tiptap-vuetify
							v-model="series.comment" :extensions="tipTapExtensions"
							:card-props="{ outlined: true }"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<FormActions v-if="initialized" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script>
import Autocomplete from '@/components/Autocomplete.vue'
import FormActions from '@/components/FormActions.vue'
import SectionCard from '@/components/SectionCard.vue'
import { tipTapExtensions } from '@/helpers/fields'
import { mandatory } from '@/helpers/rules'
import modelEditMixin from '@/mixins/model-edit'
import seriesService from '@/services/series-service'

export default {
	name: 'SeriesEdit',

	components: {
		Autocomplete,
		FormActions,
		SectionCard
	},

	mixins: [modelEditMixin],

	data() {
		return {
			mixinConfig: {
				modelEdit: {
					titleKeys: {
						add: 'title.add.series',
						edit: 'title.edit.series'
					},
					saveRedirectViewName: 'SeriesView'
				}
			},

			series: { relatedSeries: [] },
			allSeries: [],
			tipTapExtensions: tipTapExtensions,

			rules: {
				name: [
					mandatory()
				]
			}
		}
	},

	methods: {
		async fetchData() {
			const allSeriesP = seriesService.findForList()

			if (this.parsedId) {
				this.series = await seriesService.findById(this.parsedId)
			}

			this.allSeries = await allSeriesP
		},

		saveHandler() {
			return seriesService.save(this.series)
		}
	}
}
</script>
