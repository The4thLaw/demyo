<template>
	<TaxonLink v-if="!loading" :model="tag" :label="label" />
</template>

<script setup lang="ts">
import tagService from '@/services/taxon-service'

const props = defineProps<{
	tagId: number | string
	label?: string
}>()
const loading = ref(true)
const tag = ref({} as Taxon)

async function load(): Promise<void> {
	tag.value = await tagService.findById(props.tagId as number)
	loading.value = false
}

void load()
</script>
