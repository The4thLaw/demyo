<template>
	<v-container>
		<v-form ref="form">
			<SectionCard :loading="loading">
				<v-row>
					<v-col cols="12">
						<v-text-field
							v-model="source.name" :label="$t('field.DerivativeSource.name')"
							:rules="rules.name" required
						/>
					</v-col>
				</v-row>
			</SectionCard>
			<SectionCard :subtitle="$t('fieldset.DerivativeSource.contact')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field v-model="source.owner" :label="$t('field.DerivativeSource.owner')" />
					</v-col>
					<v-col cols="12" md="4">
						<v-checkbox v-model="source.active" :label="$t('field.DerivativeSource.active.edit')" />
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="source.phoneNumber" :label="$t('field.DerivativeSource.phoneNumber')"
							:rules="rules.phoneNumber"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="source.email" :label="$t('field.DerivativeSource.email')"
							:rules="rules.email"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="source.website" :label="$t('field.DerivativeSource.website')"
							:rules="rules.website"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-textarea
							v-model="source.address" :label="$t('field.DerivativeSource.address')"
							rows="3"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('field.DerivativeSource.history')" :loading="loading">
				<RichTextEditor v-model="source.history" />
			</SectionCard>

			<FormActions v-if="!loading" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleEdit } from '@/composables/model-edit'
import { email, mandatory, phone, url } from '@/helpers/rules'
import sourceService from '@/services/derivative-source-service'

async function fetchData(id :number | undefined): Promise<Partial<DerivativeSource>> {
	if (id) {
		return sourceService.findById(id)
	}
	return Promise.resolve({
		active: true
	})
}

const { model: source, loading, save, reset } = useSimpleEdit(fetchData, sourceService, [],
	'title.add.derivativeSource', 'title.edit.derivativeSource', 'DerivativeSourceView')

const rules = {
	name: [
		mandatory()
	],
	email: [
		email()
	],
	website: [
		url()
	],
	phoneNumber: [
		phone()
	]
}
</script>
