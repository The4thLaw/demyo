<template>
	<div v-if="value" class="my-4">
		<div class="c-FieldValue__label">
			{{ label }}
		</div>
		<slot />
	</div>
</template>

<script>
export default {
	name: 'FieldValue',

	props: {
		value: {
			type: null /* any */,
			required: true
		},

		label: {
			type: String,
			required: true
		}
	}
}
</script>

<style lang="less">
.c-FieldValue__label {
	color: var(--v-primary-base);
	font-size: 0.9em;
}
</style>
