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

<script setup lang="ts">
import { useCurrencies } from '@/composables/currency'

const props = defineProps<{
	prices: AbstractPrice<unknown, IModel>[]
	modelName: string
}>()

const { qualifiedPrices } = useCurrencies(computed(() => props.prices.map(p => p.price)))
</script>
