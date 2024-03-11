<template>
	<v-text-field
		v-model="inputVal" :label="$t(labelKey)"
		:prefix="prefix" :suffix="suffix"
		type="number" inputmode="decimal" step="any"
	/>
</template>

<script>
import { getCurrencySymbol, isCurrencyPrefix } from '@/helpers/i18n'
import { useReaderStore } from '@/stores/reader'
import { mapState } from 'pinia'

export default {
	name: 'CurrencyField',

	props: {
		// Using v-bind="$attrs" means we can avoid re-declaring everything we want to pass as-is to v-text-field
		value: {
			type: null,
			default: null
		},

		labelKey: {
			type: String,
			required: true
		}
	},

	data() {
		return {
			inputVal: this.value
		}
	},

	computed: {
		prefix() {
			return isCurrencyPrefix() ? getCurrencySymbol(this.currency) : null
		},

		suffix() {
			return isCurrencyPrefix() ? null : getCurrencySymbol(this.currency)
		},

		...mapState(useReaderStore, {
			currency: function (store) {
				return store.currentReader?.configuration?.currency
			}
		})
	},

	watch: {
		value(val) {
			this.inputVal = val
		},

		inputVal(val) {
			if (typeof val === 'string') {
				val = parseFloat(val)
			}
			if (isNaN(val)) {
				val = null
			}
			this.$emit('input', val)
		}
	}
}
</script>
