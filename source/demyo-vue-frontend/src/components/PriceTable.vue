<template>
	<FieldValue :label="$t(`field.${modelName}.prices.history`)">
		<v-table>
			<template #default>
				<thead>
					<tr>
						<th scope="col">
							{{ $t(`field.${modelName}.prices.date`) }}
						</th>
						<th scope="col">
							{{ $t(`field.${modelName}.prices.price`) }}
						</th>
					</tr>
				</thead>
				<tbody>
					<!-- Note: keyed by index, which is not ideal, because the price doesn't have a technical ID -->
					<tr v-for="(price, index) in prices" :key="index">
						<td>{{ $d(new Date(price.date), 'long') }}</td>
						<td>{{ qualifiedPrices[index] }}</td>
					</tr>
				</tbody>
			</template>
		</v-table>
	</FieldValue>
</template>

<script>
import FieldValue from '@/components/FieldValue.vue'
import { useCurrencies } from '@/composables/currency'

export default {
	name: 'PriceTable',

	components: {
		FieldValue
	},

	props: {
		prices: {
			type: Array,
			required: true
		},

		modelName: {
			type: String,
			required: true
		}
	},

	computed: {
		qualifiedPrices() {
			return useCurrencies(this.prices.map(p => p.price)).qualifiedPrices.value
		}
	}
}
</script>
