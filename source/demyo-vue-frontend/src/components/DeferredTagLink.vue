<template>
	<TagLink v-if="!loading" :model="tag" />
</template>

<script setup lang="ts">
import tagService from '@/services/tag-service'

const props = defineProps<{
	tagId: number | string
}>()
const loading = ref(true)
const tag = ref({} as Tag)

async function load(): Promise<void> {
	tag.value = await tagService.findById(props.tagId as number)
	loading.value = false
}

void load()
</script>
