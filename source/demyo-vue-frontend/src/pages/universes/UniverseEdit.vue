<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Universe.details')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="universe.name" :label="$t('field.Universe.name')" :rules="rules.name" required
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field v-model="universe.website" :label="$t('field.Universe.website')" />
					</v-col>
					<v-col cols="12">
						<span class="dem-fieldlabel">{{ $t('field.Universe.description') }}</span>
						<RichTextEditor v-model="universe.description" />
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.images')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="universe.logo.id" :items="images" :loading="imagesLoading"
							label-key="field.Universe.logo" refreshable @refresh="loadImages"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="universe.images" :items="images" :loading="imagesLoading"
							:multiple="true"
							label-key="field.Universe.images" refreshable @refresh="loadImages"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<FormActions v-if="!loading" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleEdit } from '@/composables/model-edit'
import { useRefreshableImages } from '@/composables/refreshable-models'
import { mandatory } from '@/helpers/rules'
import universeService from '@/services/universe-service'

const { images, imagesLoading, loadImages } = useRefreshableImages()

async function fetchData(id :number | undefined): Promise<Partial<Universe>> {
	if (id) {
		return universeService.findById(id)
	}
	return Promise.resolve({
		logo: {} as Image
	})
}

const { model: universe, loading, save, reset } = useSimpleEdit(fetchData, universeService, [loadImages],
	'title.add.universe', 'title.edit.universe', 'UniverseView')

const rules = {
	name: [
		mandatory()
	]
}
</script>
