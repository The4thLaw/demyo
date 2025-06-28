<template>
	<v-col v-if="(!type || value) && hasCols" :id="tpId" :cols="cols" :md="md" />
	<Teleport defer :to="`#${tpId}`" :disabled="!hasCols">
		<div
			v-if="!type || value"
			:class="{
				'c-FieldValue': true,
				'c-FieldValue__rich-text': type === 'rich-text'
			}"
		>
			<div class="c-FieldValue__label">
				<template v-if="label">
					{{ label }}
				</template>
				<template v-if="labelKey">
					{{ $t(labelKey) }}
				</template>
			</div>
			<!-- TODO: prepend, append slots -->
			<slot v-if="hasDefaultSlot" />
			<template v-else-if="type === 'text'">
				{{ value }}
			</template>
			<RichTextValue v-else-if="type === 'rich-text'" :value="value" />
			<a v-else-if="type === 'url'" :href="value">{{ value }}</a>
			<ModelLink v-else-if="type.endsWith('View')" :model="value" :view="type" />
		</div>
	</Teleport>
</template>

<script setup lang="ts">
// The teleport is used to conditionnally provide a parent v-col element

const props = defineProps<{
	value?: unknown,
	label?: string,
	labelKey?: string,
	type?: 'rich-text' | 'text' | 'url' |
		'PublisherView'
	cols?: number
	md?: number
}>()

const tpId = `c-FieldValue__teleport_${crypto.randomUUID()}`
const hasCols = computed(() => !!props.cols || !!props.md)

// TODO: migrate to defineSlots
const slots = useSlots()
const hasDefaultSlot = computed(() => !!slots.default)
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
