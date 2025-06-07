<template>
	<ImageThumb v-if="!loading" :image="image" :alt="alt" />
</template>

<script setup lang="ts">
import imageService from '@/services/image-service'

const props = defineProps<{
	imageId: number | string
	alt?: string
}>()
const loading = ref(true)
const image = ref({} as Image)

async function load(): Promise<void> {
	image.value = await imageService.findById(props.imageId as number)
	loading.value = false
}

void load()
</script>
