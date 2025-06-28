<template>
	<v-card
		:class="{ 'c-PublisherCard__noCollections': !hasCollections }"
		:flat="true" :hover="true" class="c-PublisherCard"
	>
		<router-link
			v-ripple :to="`/publishers/${publisher.id}/view` "
			class="c-PublisherCard__title" role="heading" aria-level="3"
		>
			{{ publisher.identifyingName }}
		</router-link>
		<v-list v-if="hasCollections" density="compact">
			<!-- eslint complains about a duplicate attribute but it's correct according to the Vuetify docs -->
			<!-- eslint-disable vue/no-duplicate-attributes -->
			<v-list-item
				v-for="collection in paginatedItems"
				:key="collection.id" :to="`/collections/${collection.id}/view`"
				:title="collection.identifyingName" :title.attr="collection.identifyingName"
			/>
			<!-- eslint-enable vue/no-duplicate-attributes -->
			<!--
				Pad the last page to keep a constant height.
				If we don't pad and the grid row doesn't have an element that is tall enough, the web page will jump
				when we reach the last item page (e.g. it's suddenly 1 item long and needs a lot less space).
			-->
			<template v-if="pageCount > 1 && !hasNextPage">
				<v-list-item v-for="pad in (itemsPerPage - paginatedItems.length)" :key="pad" />
			</template>
			<ItemCardPagination
				:page-count="pageCount" :has-previous-page="hasPreviousPage"
				:has-next-page="hasNextPage" @prev-page="previousPage" @next-page="nextPage"
			/>
		</v-list>
	</v-card>
</template>

<script setup lang="ts">
import { useBasicPagination } from '@/composables/pagination'
import { useReaderStore } from '@/stores/reader'

const props = defineProps<{
	publisher: Publisher
}>()

const collections = computed(() => props.publisher.collections)
const hasCollections = computed(() => collections.value?.length)

const readerStore = useReaderStore()
const itemsPerPage = computed(() => readerStore.currentReader.configuration.subItemsInCardIndex)

const { paginatedItems, pageCount, hasNextPage, hasPreviousPage, nextPage, previousPage }
	= useBasicPagination(collections, itemsPerPage)
</script>

<style lang="scss">
@use '@/styles/mixins';

// The following allows taking the full height if the publisher has no collections
.c-PublisherCard__noCollections a.c-PublisherCard__title {
	height: 100%;
}

.v-application a.c-PublisherCard__title {
	@include mixins.dem-model-card-title;
}
</style>
