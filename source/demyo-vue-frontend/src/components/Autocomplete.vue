<template>
	<v-autocomplete
		v-model="inputVal"
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
		@update:modelValue="onUpdateSelection"
	>
		<template v-if="refreshable" #append>
			<v-btn icon size="small" variant="flat" @click.stop="$emit('refresh')">
				<v-icon>mdi-refresh</v-icon>
			</v-btn>
		</template>
	</v-autocomplete>
</template>

<script>
export default {
	name: 'AutoComplete',

	props: {
		// Using v-bind="$attrs" means we can avoid re-declaring everything we want to pass as-is to v-autocomplete
		value: {
			type: null,
			default: false
		},

		labelKey: {
			type: String,
			required: true
		},

		refreshable: {
			type: Boolean,
			default: false
		},

		loading: {
			type: Boolean,
			default: false
		},

		// Re-declared to always be able to set multiple input as chips
		multiple: {
			type: Boolean,
			default: false
		}
	},

	data() {
		return {
			inputVal: this.value,
			search: ''
		}
	},

	methods: {
		onUpdateSelection() {
			this.$emit('update:modelValue', this.inputVal)
			this.search = ''
		}
	}
}
</script>
