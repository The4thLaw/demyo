<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Reader.identification')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="reader.name" :label="$t('field.Reader.name')" :rules="rules.name" required
						/>
					</v-col>
					<v-col cols="12" md="6">
						<span class="dem-fieldlabel">
							{{ $t('field.Reader.colour') }}
						</span>
						<v-checkbox v-model="noColour" :label="$t('special.form.noColour')" />
						<v-color-picker v-if="!noColour" v-model="reader.colour" />
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
import readerService from '@/services/reader-service'

const noColour = ref(true)

async function fetchData(id: number | undefined): Promise<Partial<Reader>> {
	if (!id) {
		return Promise.resolve({} as Partial<Reader>)
	}

	return readerService.editById(id)
		.then(fetched => {
			noColour.value = !fetched.colour
			return fetched
		})
}

const { model: reader, loading, save, reset } = useSimpleEdit(fetchData, readerService, [],
	'title.add.reader', 'title.edit.reader', 'ReaderView')

watch(noColour, (newFlag) => {
	if (newFlag) {
		reader.value.colour = undefined
	}
})

const rules = {
	name: [
		mandatory()
	]
}
</script>
