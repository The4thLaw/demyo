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
		:alt="alt ?? image.identifyingName"
		class="c-ImageThumb"
	>
</template>

<script setup lang="ts">
import { getBaseImageUrl } from '@/helpers/images'

const props = withDefaults(defineProps<{
	image: Image
	variant?: 'small' | 'large' | 'full'
	alt?: string
}>(), {
	variant: 'small',
	alt: undefined
})

const baseUrl = computed(() => getBaseImageUrl(props.image))

const src = computed(() => {
	switch (props.variant) {
		case 'large':
			return `${baseUrl.value}?w=500`
		case 'full':
			return baseUrl.value
		case 'small':
		default:
			return `${baseUrl.value}?w=250`
	}
})

const srcset = computed(() => {
	switch (props.variant) {
		case 'large':
			return `${baseUrl.value}?w=500 1x, ${baseUrl.value}?w=1000 2x`
		case 'full':
			return undefined
		case 'small':
		default:
			return `${baseUrl.value}?w=250 1x, ${baseUrl.value}?w=500 2x`
	}
})
</script>

<style lang="scss">
.c-ImageThumb {
	max-width: 95%;
}
</style>
