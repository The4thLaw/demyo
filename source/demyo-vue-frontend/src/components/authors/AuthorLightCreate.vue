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
						@keydown.enter.stop.prevent="saveAndEmit"
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

const { model: author, loading, saveAndEmit } = useLightEdit(async () => Promise.resolve({}), authorService, emit)

const rules = {
	name: [
		mandatory()
	]
}
</script>
