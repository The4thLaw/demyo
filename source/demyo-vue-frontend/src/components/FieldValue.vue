<template>
	<div class="c-FieldValue">
		<div class="c-FieldValue__label">
			{{ label }}
		</div>
		<slot />
	</div>
</template>

<script setup lang="ts">
defineProps<{
	label: string
}>()
</script>

<style lang="scss">
.c-FieldValue {
	margin-top: 16px;
	margin-bottom: 16px;

	.dem-columnized & {
		// Found on CSS-tricks, ensures the field won't break across columns
		display: inline-block;
		width: 100%;
	}

	ul, ol {
		margin-left: 2em;
	}

	a {
		color: rgb(var(--v-theme-secondary));
	}
}

// Don't set margins when in a flex grid: the grid has its own margins
.col,
[class*="col-"] {
	& > .c-FieldValue {
		margin-top: 0;
		margin-bottom: 0;
	}
}

.c-FieldValue__label {
	color: rgb(var(--v-theme-primary));
	font-size: 0.9em;
}
</style>
