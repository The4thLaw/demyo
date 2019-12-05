<template>
	<v-list-item :to="to">
		<v-list-item-icon>
			<v-icon v-text="icon" />
		</v-list-item-icon>
		<v-list-item-content>
			<v-list-item-title>{{ label }}</v-list-item-title>
		</v-list-item-content>
	</v-list-item>
</template>

<script>
export default {
	name: 'AppTask',

	props: {
		label: {
			type: String,
			required: true
		},

		icon: {
			type: String,
			required: true
		},

		to: {
			type: String,
			required: false,
			default: undefined
		}
	}
}
</script>
