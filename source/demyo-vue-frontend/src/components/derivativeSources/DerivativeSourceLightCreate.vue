<template>
	<v-container fluid>
		<v-form ref="form">
			<v-row>
				<v-col cols="12">
					<v-text-field
						v-model="source.name" :label="$t('field.DerivativeSource.name')"
						:rules="rules.name" required @keydown.enter.stop.prevent="saveAndEmit"
					/>
				</v-col>
			</v-row>
			<Teleport defer :disabled="!teleportActions" :to="teleportActions">
				<FormActions v-if="!loading" :show-reset="false" :show-back="false" @save="saveAndEmit" />
			</Teleport>
		</v-form>
	</v-container>
</template>

<script setup lang="ts">
import { useLightEdit } from '@/composables/model-edit'
import { mandatory } from '@/helpers/rules'
import sourceService from '@/services/derivative-source-service'

defineProps<{
	teleportActions?: string
}>()
const emit = defineEmits<{
	save: [id: number]
}>()

const { model: source, loading, save } = useLightEdit(async () => Promise.resolve({}), sourceService)

async function saveAndEmit(e: Event|undefined): Promise<void> {
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
