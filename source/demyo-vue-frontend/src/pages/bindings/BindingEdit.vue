<template>
	<v-container>
		<v-form ref="form">
			<SectionCard :loading="loading">
				<v-row>
					<v-col cols="12">
						<v-text-field
							v-model="binding.name" :label="$t('field.Binding.name')" :rules="rules.name" required
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
import bindingService from '@/services/binding-service'

async function fetchData(id :number | undefined): Promise<Partial<Binding>> {
	if (id) {
		return bindingService.findById(id)
	}
	return Promise.resolve({})
}

const { model: binding, loading, save, reset } = useSimpleEdit(fetchData, bindingService, [],
	'title.add.binding', 'title.edit.binding', 'BindingView')

const rules = {
	name: [
		mandatory()
	]
}
</script>
