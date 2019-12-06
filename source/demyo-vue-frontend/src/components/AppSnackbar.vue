<template>
	<v-snackbar :value="shown" @input="$emit('close')">
		{{ message }}
		<v-btn color="accent" text @click="$emit('close')">
			{{ $t('core.snack.dismiss') }}
		</v-btn>
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
