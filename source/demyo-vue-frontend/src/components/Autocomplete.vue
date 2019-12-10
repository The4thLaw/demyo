<template>
	<v-autocomplete
		v-model="inputVal"
		:items="items"
		:label="$t(labelKey)"
		:clearable="clearable"
		item-text="identifyingName"
		item-value="id"
		menu-props="allowOverflow"
		:loading="loading ? 'primary' : false"
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
		value: {
			type: null,
			default: false
		},

		items: {
			type: Array,
			required: true
		},

		labelKey: {
			type: String,
			required: true
		},

		clearable: {
			type: Boolean,
			default: true
		},

		refreshable: {
			type: Boolean,
			default: false
		},

		loading: {
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
