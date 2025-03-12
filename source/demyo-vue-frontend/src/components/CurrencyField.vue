<template>
	<v-text-field
		v-model="model" :label="$t(labelKey)"
		:prefix="prefix" :suffix="suffix"
		type="number" inputmode="decimal" step="any"
	/>
</template>

<script setup lang="ts">
import { getCurrencySymbol, isCurrencyPrefix } from '@/helpers/i18n'
import { useReaderStore } from '@/stores/reader'

const model = defineModel<number | undefined>()

defineProps<{
	labelKey: string
}>()
// Using v-bind="$attrs" means we can avoid re-declaring everything we want to pass as-is to v-text-field

const readerStore = useReaderStore()
const currency = computed(() => readerStore.currentReader?.configuration?.currency)

const prefix = computed(() => isCurrencyPrefix() ? getCurrencySymbol(currency.value) : null)
const suffix = computed(() => isCurrencyPrefix() ? null : getCurrencySymbol(currency.value))
</script>
