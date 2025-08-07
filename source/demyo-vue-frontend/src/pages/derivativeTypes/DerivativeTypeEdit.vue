<template>
	<v-container>
		<v-form ref="form">
			<SectionCard :loading="loading">
				<v-row>
					<v-col cols="12">
						<v-text-field
							v-model="type.name" :label="$t('field.DerivativeType.name')" :rules="rules.name" required
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
import { mandatory } from '@/helpers/rules'
import typeService from '@/services/derivative-type-service'

async function fetchData(id :number | undefined): Promise<Partial<DerivativeType>> {
	if (id) {
		return typeService.editById(id)
	}
	return Promise.resolve({})
}

const { model: type, loading, save, reset } = useSimpleEdit(fetchData, typeService, [],
	'title.add.derivativeType', 'title.edit.derivativeType', 'DerivativeTypeView')

const rules = {
	name: [
		mandatory()
	]
}
</script>
