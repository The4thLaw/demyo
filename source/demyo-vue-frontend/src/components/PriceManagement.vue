<template>
	<!-- Forward cols attributes -->
	<v-col v-bind="$attrs">
		<label class="dem-fieldlabel">{{ $t(`field.${modelName}.prices.history`) }}</label>
		<!-- Note: keyed by index, which is not ideal, because the price doesn't have a technical ID -->
		<v-row
			v-for="(price, index) in inputVal.prices" :key="'price_' + index"
			dense class="c-PriceManagement__priceRow"
		>
			<v-col cols="12" md="6">
				<v-text-field
					v-model="price.date" :label="$t(`field.${modelName}.prices.date`)"
					type="date" :rules="rules.prices.date" required @change="emitInput"
				/>
			</v-col>
			<v-col cols="12" md="6">
				<v-text-field
					v-model="price.price" :label="$t(`field.${modelName}.prices.price`)"
					type="number" inputmode="decimal" step="any" :rules="rules.prices.price"
					required @change="emitInput"
				/>
				<v-btn icon @click="removePrice(index)">
					<v-icon>mdi-minus</v-icon>
				</v-btn>
			</v-col>
		</v-row>
		<div class="c-PriceManagement__priceAdder">
			<v-btn icon @click="addPrice">
				<v-icon>mdi-plus</v-icon>
			</v-btn>
		</div>
	</v-col>
</template>

<script>
import { mandatory, number } from '@/helpers/rules'

export default {
	name: 'PriceManagement',

	props: {
		value: {
			type: null,
			required: true
		},

		modelName: {
			type: String,
			required: true
		}
	},

	data() {
		return {
			inputVal: this.value,

			rules: {
				prices: {
					date: [mandatory(this)],
					price: [mandatory(this), number(this)]
				}
			}
		}
	},

	watch: {
		value(val) {
			this.inputVal = val
		}
	},

	methods: {
		addPrice() {
			const newPrice = {
				date: null,
				price: null
			}
			this.inputVal.prices.push(newPrice)
			this.$emit('input', this.inputVal)
		},

		removePrice(index) {
			this.inputVal.prices.splice(index, 1)
			this.$emit('input', this.inputVal)
		},

		emitInput() {
			this.$emit('input', this.inputVal)
		}
	}
}
</script>

<style lang="less">
.c-PriceManagement__priceRow {
	> :last-child {
		display: flex;
		align-items: center;

		> button {
			margin-left: 1em;
		}
	}
}

.c-PriceManagement__priceAdder {
	text-align: right;
}
</style>
