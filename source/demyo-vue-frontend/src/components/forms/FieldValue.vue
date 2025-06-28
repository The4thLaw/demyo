<template>
	<v-col v-if="(!type || value) && hasCols" :cols="cols" :md="md" class="c-FieldValue__vcol">
		<FieldValueRaw
			:value="value" :label="label" :label-key="labelKey" :type="type"
		>
			<template v-if="$slots.prepend" #prepend>
				<slot name="prepend" />
			</template>
			<template v-if="$slots.default" #default>
				<slot name="default" />
			</template>
			<template v-if="$slots.append" #append>
				<slot name="append" />
			</template>
		</FieldValueRaw>
	</v-col>
	<FieldValueRaw
		v-else-if="!type || value"
		:value="value" :label="label" :label-key="labelKey" :type="type"
	>
		<template v-if="$slots.prepend" #prepend>
			<slot name="prepend" />
		</template>
		<template v-if="$slots.default" #default>
			<slot name="default" />
		</template>
		<template v-if="$slots.append" #append>
			<slot name="append" />
		</template>
	</FieldValueRaw>
</template>

<script setup lang="ts">
// The teleport is used to conditionnally provide a parent v-col element

const props = defineProps<{
	value?: unknown,
	label?: string,
	labelKey?: string,
	type?: 'rich-text' | 'text' | 'url' |
		'PublisherView'
	cols?: number | string
	md?: number | string
}>()

const hasCols = computed(() => !!props.cols || !!props.md)

const slots = defineSlots<{
	default: () => void
	prepend: () => void
	append: () => void
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

	&.c-FieldValue__rich-text a.c-ModelLink:not(.c-TagLink) {
		// Also restore model links. See #225
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
