<template>
	<NamedLightCreate
		:service="taxonService" :teleport-actions="teleportActions" :skeleton="{ type }"
		model-name="Taxon" @save="(id: number) => emit('save', id)"
	/>
</template>

<script setup lang="ts">
import taxonService from '@/services/taxon-service'

defineProps<{
	teleportActions?: string
	type: TaxonType
}>()
const emit = defineEmits<{
	save: [id: number]
}>()
</script>
