<template>
	<v-container fluid>
		<v-form ref="form">
			<v-row>
				<v-col cols="12">
					<v-text-field
						v-model="model.name" :label="$t(`field.${modelName}.name`)" :rules="rules.name" required
						@keydown.enter.stop.prevent="saveAndEmit"
					/>
				</v-col>
			</v-row>

			<Teleport defer :disabled="!teleportActions" :to="teleportActions">
				<FormActions v-if="!loading" :show-reset="false" :show-back="false" @save="saveAndEmit" />
			</Teleport>
		</v-form>
	</v-container>
</template>

<script setup lang="ts" generic="T extends AbstractNamedModel">
import { useLightEdit } from '@/composables/model-edit'
import { mandatory } from '@/helpers/rules'
import type AbstractModelService from '@/services/abstract-model-service'

const props = defineProps<{
	service: AbstractModelService<T>
	modelName: string
	teleportActions?: string
	skeleton?: Partial<T>
}>()
const emit = defineEmits<{
	save: [id: number]
}>()

const { model, loading, saveAndEmit }
	= useLightEdit(async () => Promise.resolve(props.skeleton ?? {}), props.service, emit)

const rules = {
	name: [
		mandatory()
	]
}
</script>
