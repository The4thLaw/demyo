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
							label-key="field.Series.universe" refreshable @refresh="loadUniverses"
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
import { useSimpleEdit } from '@/composables/model-edit'
import { useRefreshableUniverses } from '@/composables/refreshable-models'
import { mandatory } from '@/helpers/rules'
import seriesService from '@/services/series-service'

const { universes, universesLoading, loadUniverses } = useRefreshableUniverses()
async function fetchData(id :number | undefined): Promise<Partial<Series>> {
	if (id) {
		return seriesService.findById(id)
	}
	return Promise.resolve({
		universe: {} as Universe
	})
}

const { model: series, loading, save, reset } = useSimpleEdit(fetchData, seriesService, [loadUniverses],
	'title.add.series', 'title.edit.series', 'SeriesView')

const rules = {
	name: [
		mandatory()
	]
}
</script>
