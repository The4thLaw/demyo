<template>
	<v-autocomplete
		v-model="model"
		v-model:search="search"
		v-bind="$attrs"
		:label="$t(labelKey, multiple ? 2 : 1)"
		item-title="identifyingName"
		item-value="id"
		:multiple="multiple"
		:chips="multiple"
		:closable-chips="multiple"
		:loading="loading ? 'primary' : false"
		:no-data-text="$t('core.components.Autocomplete.nodata')"
		@update:model-value="onUpdateSelection"
	>
		<template v-if="refreshable" #append>
			<v-btn icon size="small" variant="flat" @click.stop="emit('refresh')">
				<v-icon>mdi-refresh</v-icon>
			</v-btn>
		</template>
	</v-autocomplete>
</template>

<script setup lang="ts">
const model = defineModel<unknown>()

withDefaults(defineProps<{
	labelKey: string
	refreshable?: boolean
	loading?: boolean
	multiple?: boolean
}>(), {
	refreshable: false,
	loading: false,
	multiple: false
})

const search = ref('')

const emit = defineEmits(['refresh'])

function onUpdateSelection(): void {
	search.value = ''
}
</script>
