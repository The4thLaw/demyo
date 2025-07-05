<template>
	<div
		v-if="!type || value"
		:class="{
			'c-FieldValueRaw': true,
			'c-FieldValueRaw__rich-text': type === 'rich-text'
		}"
	>
		<div class="c-FieldValueRaw__label">
			<template v-if="label">
				{{ label }}
			</template>
			<template v-if="labelKey">
				{{ $t(labelKey) }}
			</template>
		</div>
		<slot v-if="$slots.prepend" name="prepend" />

		<slot v-if="$slots.default" />
		<template v-else-if="type === 'text'">
			{{ value }}
		</template>
		<template v-else-if="type === 'date'">
			{{ $d(new Date(value), 'long') }}
		</template>
		<RichTextValue v-else-if="type === 'rich-text'" :value="value" />
		<a v-else-if="type === 'url'" :href="value">{{ value }}</a>
		<ModelLink v-else-if="type.endsWith('View')" :model="value" :view="type" />

		<slot v-if="$slots.append" name="append" />
	</div>
</template>

<script setup lang="ts">
defineProps<{
	value?: unknown,
	label?: string,
	labelKey?: string,
	type?: 'rich-text' | 'text' | 'url' |
		'PublisherView'
}>()

defineSlots<{
	default: () => void
	prepend: () => void
	append: () => void
}>()
</script>

<style lang="scss">
.c-FieldValueRaw {
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

	&.c-FieldValueRaw__rich-text a.c-ModelLink:not(.c-TagLink) {
		// Also restore model links. See #225
		color: rgb(var(--v-theme-secondary));
	}
}

// Don't set margins when in a flex grid: the grid has its own margins
.col,
[class*="col-"] {
	& > .c-FieldValueRaw {
		margin-top: 0;
		margin-bottom: 0;
	}
}

.c-FieldValueRaw__label {
	color: rgb(var(--v-theme-primary));
	font-size: 0.9em;
}
</style>
