<template>
	<div>
		<div v-if="!splitByFirstLetter">
			<div v-for="item in items" :key="item.id">
				<slot :item="item" />
			</div>
		</div>

		<div v-if="splitByFirstLetter">
			<div v-for="(value, key) in groupedItems" :key="key">
				<div class="c-TextIndex__firstLetter display-1 mx-2 my-4 accent--text">
					{{ key }}
				</div>
				<v-card>
					<v-card-text>
						<v-list class="c-TextIndex__list" dense>
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
		<v-pagination v-if="pageCount > 1" v-model="currentPage" :length="pageCount" total-visible="10" class="my-2" />
	</div>
</template>

<script>
import { groupBy, deburr } from 'lodash'

export default {
	name: 'TextIndex',

	props: {
		items: {
			type: Array,
			required: true
		},
		splitByFirstLetter: {
			type: Boolean,
			default: true
		},
		firstLetterExtractor: {
			type: Function,
			default: () => '#'
		}
	},

	data() {
		return {
			// TODO: load this from the config
			itemsPerPage: 30,
			currentPage: 1
		}
	},

	computed: {
		paginatedItems() {
			return this.items.slice((this.currentPage-1)*this.itemsPerPage, this.currentPage*this.itemsPerPage)
		},

		groupedItems() {
			return groupBy(this.paginatedItems, (i) => deburr(this.firstLetterExtractor(i)))
		},

		pageCount() {
			return Math.ceil(this.items.length / this.itemsPerPage);
		}
	}
}
</script>

<style lang="less">
#demyo .c-TextIndex__firstLetter {
	font-family: serif !important;
}

#demyo .c-TextIndex__list {
	columns: 4;
	font-size: 1rem;

	a {
		color: inherit;
		text-decoration: none;
	}
}

// TODO: when https://github.com/vuetifyjs/vuetify/issues/2541​ is resolved, switch to it
#demyo .v-pagination button {
	box-shadow: none;
	border-radius: 50%;
}

@media (max-width: 1023px) and (min-width: 840px) {
	.c-TextIndex__list {
		columns: 3;
	}
}

@media (max-width: 839px) and (min-width: 480px) {
	.c-TextIndex__list {
		columns: 2;
	}
}

@media (max-width: 479px) {
	.mdl-list {
		columns: 1;
	}
}
</style>
