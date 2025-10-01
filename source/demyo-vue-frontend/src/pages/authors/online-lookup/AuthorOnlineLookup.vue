<template>
	<v-btn color="primary" prepend-icon="mdi-web" variant="outlined" @click="dialog = true">
		{{ $t('page.AuthorOnlineLookup.startLookup') }}
	</v-btn>
	<v-dialog v-model="dialog" max-width="max(50vw, 45em)">
		<AOLWizard
			v-if="dialog" :term="term"
			@apply="apply"
			@close="dialog = false"
		/>
	</v-dialog>
</template>

<script setup lang="ts">
defineProps<{
	term: string
}>()

const emit = defineEmits<{
	apply: [author: Partial<Author>]
}>()

const dialog = ref(false)

function apply(author: Partial<Author>): void {
	// Filter out empty values to avoid overwrites
	if (!author?.firstName) {
		delete author.firstName
	}
	if (!author?.name) {
		delete author.name
	}
	if (!author?.nativeLanguageName) {
		delete author.nativeLanguageName
	}
	if (!author?.country) {
		delete author.country
	}
	if (!author?.birthDate) {
		delete author.birthDate
	}
	if (!author?.deathDate) {
		delete author.deathDate
	}
	if (!author?.biography) {
		delete author.biography
	}

	emit('apply', author)
	dialog.value = false
}
</script>
