<template>
	<v-container fluid>
		<v-form ref="form">
			<v-row>
				<v-col cols="12">
					<v-text-field v-model="author.firstName" :label="$t('field.Author.firstName')" />
				</v-col>
				<v-col cols="12">
					<v-text-field
						v-model="author.name" :label="$t('field.Author.name')" :rules="rules.name" required
					/>
				</v-col>
				<Teleport defer :disabled="!teleportActions" :to="teleportActions">
					<FormActions v-if="!loading" :show-reset="false" :show-back="false" @save="saveAndEmit" />
				</Teleport>
			</v-row>
		</v-form>
	</v-container>
</template>

<script setup lang="ts">
import { useLightEdit } from '@/composables/model-edit'
import { mandatory } from '@/helpers/rules'
import authorService from '@/services/author-service'

defineProps<{
	teleportActions?: string
}>()
const emit = defineEmits<{
	save: [id: number]
}>()

async function fetchData(): Promise<Partial<Author>> {
	return Promise.resolve({})
}

const { model: author, loading, save } = useLightEdit(fetchData, authorService, [])

async function saveAndEmit(): Promise<void> {
	const id = await save()
	if (id) {
		// Only emit if the save was successful
		emit('save', id)
	}
}

const rules = {
	name: [
		mandatory()
	]
}
</script>
