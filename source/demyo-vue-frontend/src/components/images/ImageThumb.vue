<template>
	<img
		v-if="image?.identifyingName"
		v-fullscreen-image="{
			imageUrl: baseUrl,
			withDownload: false,
			maxHeight: '100vh'
		}"
		:src="src"
		:srcset="srcset"
		:alt="image.identifyingName"
	>
</template>

<script setup lang="ts">
import { getBaseImageUrl } from '@/helpers/images'

const props = withDefaults(defineProps<{
	image: Image
	variant?: 'small' | 'large' | 'full'
}>(), {
	variant: 'small'
})

const baseUrl = computed(() => getBaseImageUrl(props.image))

const src = computed(() => {
	switch (props.variant) {
		case 'small':
			return `${baseUrl.value}?w=250`
		case 'large':
			return `${baseUrl.value}?w=500`
		case 'full':
			return baseUrl.value
		default:
			// eslint-disable-next-line @typescript-eslint/restrict-template-expressions
			throw new Error(`Unsupported thumb variant ${props.variant}`)
	}
})

const srcset = computed(() => {
	switch (props.variant) {
		case 'small':
			return `${baseUrl.value}?w=250 1x, ${baseUrl.value}?w=500 2x`
		case 'large':
			return `${baseUrl.value}?w=500 1x, ${baseUrl.value}?w=1000 2x`
		case 'full':
			return undefined
		default:
			// eslint-disable-next-line @typescript-eslint/restrict-template-expressions
			throw new Error(`Unsupported thumb variant ${props.variant}`)
	}
})
</script>
