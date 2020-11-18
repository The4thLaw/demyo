<template>
	<v-autocomplete
		v-model="inputVal"
		v-bind="$attrs"
		:label="$tc(labelKey, multiple ? 2 : 1)"
		item-text="identifyingName"
		item-value="id"
		menu-props="allowOverflow"
		:multiple="multiple"
		:chips="multiple"
		:deletable-chips="multiple"
		:loading="loading ? 'primary' : false"
		:no-data-text="$t('core.components.Autocomplete.nodata')"
	>
		<template v-if="refreshable" v-slot:append-outer>
			<v-btn icon small @click.stop="$emit('refresh')">
				<v-icon>mdi-refresh</v-icon>
			</v-btn>
		</template>
	</v-autocomplete>
</template>

<script>
export default {
	name: 'Autocomplete',

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
			inputVal: this.value
		}
	},

	watch: {
		value(val) {
			this.inputVal = val
		},

		inputVal(val) {
			this.$emit('input', val)
		}
	}
}
</script>
