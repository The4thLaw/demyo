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

<script>
export default {
	name: 'AppSnackbar',

	props: {
		shown: {
			type: Boolean,
			required: true
		},

		message: {
			type: String,
			required: false,
			default: ''
		}
	}
}
</script>
