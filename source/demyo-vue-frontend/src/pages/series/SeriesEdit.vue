<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Series.identification')" :loading="loading">
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
							v-model="series.universe.id" :items="universes" :loading="universesLoading"
							:add-component="UniverseLightCreate" add-label="title.add.universe"
							label-key="field.Series.universe" refreshable @refresh="loadUniverses"
							@added="(id: number) => series.universe.id = id"
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
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="series.genres" :items="genres" :loading="taxonsLoading"
							multiple clearable
							:add-component="TaxonLightCreate" :add-props="{ type: 'GENRE' }"
							add-label="title.add.taxon.GENRE"
							label-key="field.Taxonomized.genres" refreshable @refresh="loadTaxons"
							@added="(id: number) => series.genres.push(id)"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="series.tags" :items="tags" :loading="taxonsLoading"
							multiple clearable
							:add-component="TaxonLightCreate" :add-props="{ type: 'TAG' }"
							add-label="title.add.taxon.TAG"
							label-key="field.Taxonomized.tags" refreshable @refresh="loadTaxons"
							@added="(id: number) => series.tags.push(id)"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Series.freeText')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<span class="dem-fieldlabel">{{ $t('field.Series.summary') }}</span>
						<RichTextEditor v-model="series.summary" />
					</v-col>
					<v-col cols="12" md="6">
						<span class="dem-fieldlabel">{{ $t('field.Series.comment') }}</span>
						<RichTextEditor v-model="series.comment" />
					</v-col>
				</v-row>
			</SectionCard>

			<FormActions v-if="!loading" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script setup lang="ts">
import TaxonLightCreate from '@/components/tags/TaxonLightCreate.vue'
import UniverseLightCreate from '@/components/universes/UniverseLightCreate.vue'
import { useSimpleEdit } from '@/composables/model-edit'
import { useRefreshableTaxons, useRefreshableUniverses } from '@/composables/refreshable-models'
import { mandatory } from '@/helpers/rules'
import seriesService from '@/services/series-service'

const { genres, tags, taxonsLoading, loadTaxons } = useRefreshableTaxons()
const { universes, universesLoading, loadUniverses } = useRefreshableUniverses()
async function fetchData(id :number | undefined): Promise<Partial<Series>> {
	if (id) {
		return seriesService.editById(id)
	}
	return Promise.resolve({
		universe: {} as Universe
	})
}

const { model: series, loading, save, reset } = useSimpleEdit(fetchData, seriesService, [loadTaxons, loadUniverses],
	'title.add.series', 'title.edit.series', 'SeriesView')

const rules = {
	name: [
		mandatory()
	]
}
</script>
