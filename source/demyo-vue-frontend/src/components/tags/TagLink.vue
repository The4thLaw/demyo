<template>
	<ModelLink :model="model" :comma-separated="false" css-class="c-TagLink" view="TagView">
		<template #default="slotProps">
			<span :style="getStyle(slotProps.item)" class="d-Tag">
				<template v-if="label">label</template>
				<template v-else>{{ slotProps.item.identifyingName }}</template>
				<span
					v-if="hasCount(slotProps.item)"
					class="d-Tag__count" v-text="slotProps.item.usageCount"
				/>
			</span>
			<!-- Need to allow line breaks between tags -->
			<wbr>
		</template>
	</ModelLink>
</template>

<script setup lang="ts">
import { getStyle } from '@/composables/tags'

defineProps<{
	model: IModel
	label?: string
}>()

function hasCount(tag: ProcessedTag): boolean {
	return tag.usageCount >= 0
}
</script>

<style lang="scss">
.d-Tag {
	border-top-left-radius: 6px;
	border-top-right-radius: 6px;
	border-bottom-right-radius: 6px;
	background-color: rgb(var(--v-theme-surface-light));
	margin-right: 1em;
	padding: 4px 12px;
	white-space: nowrap;
	line-height: 200%;

	h1 &,
	h2 &,
	h3 & {
		// Avoid overflows in titles containing tags
		white-space: initial;
		line-height: 100%;
	}

	&__count {
		font-size: 80%;

		&::before {
			content: "(";
			margin-left: 0.5em;
		}

		&::after {
			content: ")";
		}
	}
}

a.c-TagLink:hover {
	text-decoration: none !important;
}
</style>
