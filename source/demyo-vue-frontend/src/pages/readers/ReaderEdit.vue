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
						<label class="dem-fieldlabel">
							{{ $t('field.Reader.colour') }}
						</label>
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

function fetchData(id: number|undefined): Promise<Partial<Reader>> {
	if (!id) {
		const reader: Partial<Reader> = {}
		return Promise.resolve(reader)
	}

	return readerService.findById(id)
		.then(reader => {
			noColour.value = !reader.colour
			return reader
		})
}

const {model: reader, loading, save, reset} = useSimpleEdit(fetchData, readerService, [],
	'title.add.reader', 'title.edit.reader', 'ReaderView')

watch(noColour, async (newFlag) => {
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
