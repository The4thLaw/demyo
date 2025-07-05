<template>
	<NamedLightCreate
		:service="universeService" :teleport-actions="teleportActions" model-name="Universe"
		@save="(id: number) => emit('save', id)"
	/>
</template>

<script setup lang="ts">
import universeService from '@/services/universe-service'

defineProps<{
	teleportActions?: string
}>()
const emit = defineEmits<{
	save: [id: number]
}>()
</script>
