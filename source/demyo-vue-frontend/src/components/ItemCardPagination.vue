<template>
	<v-list-item v-if="pageCount > 1" class="c-ItemCardPagination">
		<!-- Custom pagination because vuetify doesn't provide no-count pagination -->
		<v-btn :disabled="!hasPreviousPage" text icon @click.stop="$emit('prev-page')">
			<v-icon>mdi-chevron-left</v-icon>
		</v-btn>
		<v-btn :disabled="!hasNextPage" text icon @click.stop="$emit('next-page')">
			<v-icon>mdi-chevron-right</v-icon>
		</v-btn>
	</v-list-item>
</template>

<script>
export default {
	name: 'ItemCardPagination',

	props: {
		pageCount: {
			type: Number,
			required: true
		},

		hasPreviousPage: {
			type: Boolean,
			required: true
		},

		hasNextPage: {
			type: Boolean,
			required: true
		}
	}
}
</script>

<style lang="less">
// Align the pagination buttons
.c-ItemCardPagination {
	justify-content: space-around;

	&.v-list-item::after {
		display: none;
	}
}
</style>
