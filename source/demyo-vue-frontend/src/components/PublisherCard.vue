<template>
	<v-card
		:class="{ 'c-PublisherCard__noCollections': !hasCollections }"
		:flat="true" :hover="true" class="c-PublisherCard"
	>
		<router-link
			v-ripple :to="`/publishers/${publisher.id}/view` "
			class="c-PublisherCard__title" role="heading" arial-level="3"
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

<script>
import ItemCardPagination from '@/components/ItemCardPagination.vue'
import paginatedTextMixin from '@/mixins/paginated-text'
import { useReaderStore } from '@/stores/reader'
import { mapState } from 'pinia'

export default {
	name: 'PublisherCard',

	components: {
		ItemCardPagination
	},

	mixins: [paginatedTextMixin],

	props: {
		publisher: {
			type: null,
			required: true
		}
	},

	computed: {
		// Having these allows us to reuse the logic from paginated-text
		itemsToPaginate() {
			return this.publisher.collections || []
		},

		...mapState(useReaderStore, {
			itemsPerPage: store => store.currentReader.configuration.subItemsInCardIndex
		}),

		hasCollections() {
			return this.publisher.collections && this.publisher.collections.length > 0
		}
	},

	methods: {
		// Having this allows us to reuse the logic from paginated-text
		firstLetterExtractor: () => '#'
	}
}
</script>

<style lang="less">
@import "../styles/detached-rulesets.less";

// Override default style since the card cannot be clicked
.c-PublisherCard.v-card--hover {
	@dem-dr-model-card--hover();
}

// The following allows taking the full height if the publisher has no collections
.c-PublisherCard__noCollections a.c-PublisherCard__title {
	height: 100%;
}

.v-application a.c-PublisherCard__title {
	@dem-dr-model-card-title();
}
</style>
