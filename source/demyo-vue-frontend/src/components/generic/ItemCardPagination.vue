<template>
	<v-list-item v-if="pageCount > 1" class="c-ItemCardPagination">
		<!-- Custom pagination because vuetify doesn't provide no-count pagination -->
		<v-btn :disabled="!hasPreviousPage" variant="text" icon @click.stop="emit('prev-page')">
			<v-icon>mdi-chevron-left</v-icon>
		</v-btn>
		<v-btn :disabled="!hasNextPage" variant="text" icon @click.stop="emit('next-page')">
			<v-icon>mdi-chevron-right</v-icon>
		</v-btn>
	</v-list-item>
</template>

<script setup lang="ts">
defineProps<{
	pageCount: number,
	hasPreviousPage: boolean,
	hasNextPage: boolean
}>()

const emit = defineEmits(['prev-page', 'next-page'])
</script>

<style lang="scss">
// Align the pagination buttons: have the pagination take the full width,
// its content as well, and the buttons spaced evenly
.c-ItemCardPagination {
	display: flex;

	.v-list-item__content {
		flex: 1;
		display: flex;
		justify-content: space-around;
	}
}
</style>
