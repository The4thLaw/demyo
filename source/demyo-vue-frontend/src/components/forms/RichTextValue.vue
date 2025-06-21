<template>
	<component :is="{ template: processedValue }" />
</template>

<script setup lang="ts">
import { useRichTextTemplate } from '@/composables/rich-text'

const props = defineProps<{
	value?: string
}>()

const processedValue = useRichTextTemplate(computed(() => props.value))
</script>
