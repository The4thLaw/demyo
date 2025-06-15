<template>
	<FieldValue v-if="value" :label="label" rich-text>
		!!!!!! <component :is="{ template: processedValue }" />
	</FieldValue>
</template>

<script setup lang="ts">
import { useRichTextTemplate } from '@/composables/rich-text'

const props = defineProps<{
	value?: string
}>()

const processedValue = useRichTextTemplate(computed(() => props.value))
</script>
