<template>
	<div
		ref="keyTarget"
		v-touch="{
			left: nextPage,
			right: previousPage
		}"
		class="c-TextIndex"
		:class="{ 'c-TextIndex__compact': compact }"
		@keyup.arrow-left.exact="previousPage()"
		@keyup.arrow-right.exact="nextPage()"
	>
		<div v-if="!splitByFirstLetter">
			<v-card :flat="compact">
				<v-card-text>
					<v-list class="dem-columnized c-TextIndex__list" dense>
						<v-list-item v-for="item in paginatedItems" :key="item.id">
							<v-list-item-content>
								<slot :item="item" />
							</v-list-item-content>
						</v-list-item>
					</v-list>
				</v-card-text>
			</v-card>
		</div>

		<div v-if="splitByFirstLetter">
			<div v-for="(value, letter) in groupedItems" :key="letter">
				<h2 class="c-TextIndex__firstLetter text-h4 mx-2 my-4 accent--text">
					{{ letter }}
				</h2>
				<v-card :flat="compact">
					<v-card-text>
						<v-list class="dem-columnized c-TextIndex__list" dense>
							<v-list-item v-for="item in value" :key="item.id">
								<v-list-item-content>
									<slot :item="item" />
								</v-list-item-content>
							</v-list-item>
						</v-list>
					</v-card-text>
				</v-card>
			</div>
		</div>
		<v-pagination
			v-if="pageCount > 1"
			v-model="currentPage"
			:length="pageCount"
			total-visible="10"
			class="my-2"
			@input="$emit('page-change')"
		/>
	</div>
</template>

<script>
import { focusElement } from '@/helpers/dom'
import paginatedTextMixin from '@/mixins/paginated-text'

export default {
	name: 'TextIndex',

	mixins: [paginatedTextMixin],

	props: {
		splitByFirstLetter: {
			type: Boolean,
			default: true
		},
		firstLetterExtractor: {
			type: Function,
			default: () => '#'
		},
		compact: {
			type: Boolean,
			default: false
		}
	},

	mounted() {
		focusElement(this.$refs.keyTarget)
	}
}
</script>

<style lang="less">
.c-TextIndex {
	// No outline on this artifically focused element
	outline: 0;
}

.c-TextIndex__compact {
	.v-card__text {
		padding: 0;
	}
}

#demyo .c-TextIndex__firstLetter {
	font-family: serif !important;
}

#demyo .c-TextIndex__list {
	font-size: 1rem;
	column-count: initial;
	column-width: 15em;

	a {
		color: inherit;
		text-decoration: none;

		&:hover {
			color: var(--v-anchor-base);
			text-decoration: underline;
		}
	}
}
</style>
