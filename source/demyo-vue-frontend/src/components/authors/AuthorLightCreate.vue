<template>
	<v-container fluid>
		<v-form ref="form">
			<AuthorOnlineLookup
				:term="`${author.firstName ?? ''} ${author.name ?? ''}`"
				@apply="applyOnlineLookup"
			/>
			<v-row no-gutters class="mt-4">
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
			<v-alert
				v-if="hasAdditionalFields"
				border="start" type="info" class="my-4" variant="outlined"
			>
				{{ $t('page.AuthorOnlineLookup.additionalFields') }}
			</v-alert>
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

function applyOnlineLookup(online: Partial<Author>): void {
	author.value = {
		...author.value,
		...online
	}
}

const hasAdditionalFields = computed(() => {
	const a = author.value
	return !!a.nativeLanguageName || !!a.country || !!a.birthDate || !!a.deathDate || !!a.biography
})

const rules = {
	name: [
		mandatory()
	]
}
</script>
