<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Taxon.identity')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="taxon.name" :label="$t('field.Taxon.name')" :rules="rules.name" required
						/>
					</v-col>
					<v-col cols="12" md="6">
						<span class="dem-fieldlabel">
							{{ $t('field.Taxon.preview') }}
						</span>
						<div>
							<span :style="style" class="d-Taxon">
								{{ taxon.name }}
							</span>
						</div>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="6">
						<span class="dem-fieldlabel">{{ $t('field.Taxon.description') }}</span>
						<RichTextEditor v-model="taxon.description" />
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Taxon.colours')">
				<v-row>
					<v-col cols="12" md="6">
						<span class="dem-fieldlabel">
							{{ $t('field.Taxon.fgColour') }}
						</span>
						<v-checkbox v-model="noFgColour" :label="$t('special.form.noColour')" />
						<v-color-picker v-if="!noFgColour" v-model="taxon.fgColour" />
					</v-col>
					<v-col cols="12" md="6">
						<span class="dem-fieldlabel">
							{{ $t('field.Taxon.bgColour') }}
						</span>
						<v-checkbox v-model="noBgColour" :label="$t('special.form.noColour')" />
						<v-color-picker v-if="!noBgColour" v-model="taxon.bgColour" />
					</v-col>
				</v-row>
			</SectionCard>

			<FormActions v-if="!loading" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleEdit } from '@/composables/model-edit'
import { useTaxonStyle } from '@/composables/taxons'
import { mandatory } from '@/helpers/rules'
import taxonService from '@/services/taxon-service'

const props = defineProps<{
	type?: TaxonType
}>()

const noFgColour = ref(true)
const noBgColour = ref(true)

async function fetchData(id: number | undefined): Promise<Partial<Taxon>> {
	if (!id) {
		const skeleton: Partial<Taxon> = {
			type: props.type
		}
		return Promise.resolve(skeleton)
	}

	return taxonService.editById(id)
		.then(t => {
			noFgColour.value = !t.fgColour
			noBgColour.value = !t.bgColour
			return t
		})
}

const { model: taxon, loading, save, reset } = useSimpleEdit(fetchData, taxonService,
	[], (t: Partial<Taxon>) => `title.add.taxon.${t.type}`,
	(t: Partial<Taxon>) => `title.edit.taxon.${t.type}`, 'TaxonView')

const style = useTaxonStyle(taxon)

watch(noBgColour, (newFlag) => {
	if (newFlag) {
		taxon.value.bgColour = undefined
	}
})
watch(noFgColour, (newFlag) => {
	if (newFlag) {
		taxon.value.fgColour = undefined
	}
})

const rules = {
	name: [
		mandatory()
	]
}
</script>
