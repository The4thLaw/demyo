<template>
	<NamedLightCreate
		:service="tagService" :teleport-actions="teleportActions" model-name="Tag"
		@save="(id: number) => emit('save', id)"
	/>
</template>

<script setup lang="ts">
import tagService from '@/services/tag-service'

defineProps<{
	teleportActions?: string
}>()
const emit = defineEmits<{
	save: [id: number]
}>()
</script>
