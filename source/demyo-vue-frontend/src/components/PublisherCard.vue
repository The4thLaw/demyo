<template>
	<v-card
		:class="{ 'c-PublisherCard__noCollections': !hasCollections }"
		:flat="true" :hover="true" class="c-PublisherCard"
	>
		<template>
			<router-link
				v-ripple :to="`/publishers/${publisher.id}/view` "
				class="c-PublisherCard__title" role="heading" arial-level="3"
			>
				{{ publisher.identifyingName }}
			</router-link>
			<v-list v-if="hasCollections" dense>
				<v-list-item
					v-for="collection in paginatedItems"
					:key="collection.id" :href="`/collections/${collection.id}/view`"
				>
					<v-list-item-content>
						<v-list-item-title :title="collection.identifyingName">
							{{ collection.identifyingName }}
						</v-list-item-title>
					</v-list-item-content>
				</v-list-item>
				<!--
					Pad the last page to keep a constant height.
					If we don't pad and the grid row doesn't have an element that is tall enough, the web page will jump
					when we reach the last item page (e.g. it's suddenly 1 item long and needs a lot less space).
				-->
				<template v-if="pageCount > 1 && !hasNextPage">
					<v-list-item v-for="pad in (itemsPerPage - paginatedItems.length)" :key="pad" />
				</template>
				<v-list-item v-if="pageCount > 1" class="c-PublisherCard__pagination">
					<!-- Custom pagination because vuetify doesn't provide no-count pagination -->
					<v-btn :disabled="!hasPreviousPage" text icon @click.stop="previousPage">
						<v-icon>mdi-chevron-left</v-icon>
					</v-btn>
					<v-btn :disabled="!hasNextPage" text icon @click.stop="nextPage">
						<v-icon>mdi-chevron-right</v-icon>
					</v-btn>
				</v-list-item>
			</v-list>
		</template>
	</v-card>
</template>

<script>
import { mapState } from 'vuex'
import paginatedTextMixin from '@/mixins/paginated-text'

export default {
	name: 'PublisherCard',

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

		...mapState({
			itemsPerPage: state => state.reader.currentReader.configuration.subItemsInCardIndex || 5
		}),

		hasCollections() {
			return this.publisher.collections && this.publisher.collections.length >= 0
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

// Align the pagination buttons
.c-PublisherCard__pagination {
	@dem-dr-model-card-pagination();
}
</style>
