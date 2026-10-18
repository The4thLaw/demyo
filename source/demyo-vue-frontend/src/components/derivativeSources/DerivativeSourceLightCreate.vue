<template>
	<NamedLightCreate
		:service="derivativeSourceService" :teleport-actions="teleportActions"
		:skeleton="{ active: true }" model-name="DerivativeSource"
		@save="(id: number) => emit('save', id)"
	/>
</template>

<script setup lang="ts">
import derivativeSourceService from '@/services/derivative-source-service'

defineProps<{
	teleportActions?: string
}>()
const emit = defineEmits<{
	save: [id: number]
}>()
</script>
