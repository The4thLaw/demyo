<template>
	<v-snackbar :model-value="shown" @update:modelValue="$emit('close')">
		{{ message }}
		<template #actions>
			<v-btn color="secondary" variant="text" @click="$emit('close')">
				{{ $t('core.snack.dismiss') }}
			</v-btn>
		</template>
	</v-snackbar>
</template>

<script setup lang="ts">
defineProps<{
	shown: boolean,
	message?: string
}>()

const emit = defineEmits(['close'])
</script>
