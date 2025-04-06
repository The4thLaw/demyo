<template>
	<!-- Forward cols attributes -->
	<v-col v-bind="$attrs">
		<span class="dem-fieldlabel">{{ $t(`field.${modelName}.prices.history`) }}</span>
		<!-- Note: keyed by index, which is not ideal, because the price doesn't have a technical ID -->
		<v-row
			v-for="(price, index) in model.prices" :key="'price_' + index"
			dense class="c-PriceManagement__priceRow"
		>
			<v-col cols="12" md="6">
				<v-text-field
					v-model="price.date" :label="$t(`field.${modelName}.prices.date`)"
					type="date" :rules="rules.prices.date" required
				/>
			</v-col>
			<v-col cols="12" md="6">
				<CurrencyField
					v-model="price.price" :label-key="`field.${modelName}.prices.price`"
					:rules="rules.prices.price" required
				/>
				<v-btn icon size="small" variant="flat" @click="removePrice(index)">
					<v-icon>mdi-minus</v-icon>
				</v-btn>
			</v-col>
		</v-row>
		<div class="c-PriceManagement__priceAdder">
			<v-btn icon size="small" variant="flat" @click="addPrice">
				<v-icon>mdi-plus</v-icon>
			</v-btn>
		</div>
	</v-col>
</template>

<script setup lang="ts">
import { mandatory, number } from '@/helpers/rules'

const model = defineModel<AbstractPricedModel<AbstractPrice<unknown, IModel>, IModel>>()
defineProps<{
	modelName: string
}>()

function addPrice() {
	const newPrice: Partial<AbstractPrice<unknown, IModel>> = {
		date: undefined,
		price: undefined
	}
	model.value?.prices.push(newPrice as AbstractPrice<unknown, IModel>)
}

function removePrice(index: number) {
	model.value?.prices.splice(index, 1)
}

const rules = {
	prices: {
		date: [mandatory()],
		price: [mandatory(), number()]
	}
}
</script>

<style lang="scss">
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
