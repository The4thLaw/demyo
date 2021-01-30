<template>
	<FieldValue :label="$t(`field.${modelName}.prices.history`)">
		<v-simple-table>
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
					<!-- Note: keyed by index, which is not ideal,
					because the price doesn't have a technical ID -->
					<tr v-for="(price, index) in prices" :key="index">
						<td>{{ $d(new Date(price.date), 'long') }}</td>
						<td>{{ price.price }}</td>
					</tr>
				</tbody>
			</template>
		</v-simple-table>
	</FieldValue>
</template>

<script>
import FieldValue from '@/components/FieldValue'

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
	}
}
</script>
