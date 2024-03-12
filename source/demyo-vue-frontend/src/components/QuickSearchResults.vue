<template>
	<SectionCard :loading="loading" class="c-QuickSearchResults">
		<template v-if="hasResults">
			<template v-for="key in modelTypes">
				<template v-if="results[key]">
					<h2 :key="`ti-${key}-title`" class="text-subtitle-1 primary--text">
						{{ $t('quicksearch.results.title.' + key) }}
					</h2>
					<v-list :key="`ti-${key}`" class="dem-columnized c-QuickSearchResults__list" dense>
						<v-list-item v-for="item in results[key]" :key="item.id">
							<v-list-item-content>
								<div>
									<router-link :to="`/${key}/${item.id}/view`" @click.native="$emit('click')">
										<template v-if="key !== 'albums'">
											{{ item.identifyingName }}
										</template>
										<template v-else>
											{{ item.title }}
										</template>
									</router-link>
								</div>
								<div v-if="key === 'albums' && item.series" class="c-QuickSearchResults__albumSeries">
									<router-link :to="`/series/${item.series.id}/view`" @click="$emit('click')">
										{{ item.series.identifyingName }}
									</router-link>
								</div>
							</v-list-item-content>
						</v-list-item>
					</v-list>
				</template>
			</template>
		</template>
		<template v-else-if="hasLoadedResults">
			<v-alert border="left" type="info" text class="my-4">
				{{ $t('quicksearch.noResults') }}
			</v-alert>
		</template>
	</SectionCard>
</template>

<script>
import SectionCard from '@/components/SectionCard.vue'

export default {
	name: 'QuickSearchResults',

	components: {
		SectionCard
	},

	props: {
		results: {
			type: null,
			required: true
		},

		loading: {
			type: Boolean,
			default: false
		}
	},

	data() {
		return {
			modelTypes: ['series', 'albums', 'tags', 'authors', 'publishers', 'collections']
		}
	},

	computed: {
		hasLoadedResults() {
			return this.results !== undefined
		},

		hasResults() {
			return this.results && Object.keys(this.results).length > 0
		}
	}
}
</script>

<style lang="less">
#demyo .c-QuickSearchResults .v-list-item__title {
	font-size: inherit;
	font-weight: normal;
	margin-bottom: 8px;
}

.c-QuickSearchResults .c-TextIndex {
	margin-bottom: 16px;
}

.c-QuickSearchResults__list.dem-columnized {
	font-size: 1rem;
	column-count: initial;
	column-width: 15em;
	margin-bottom: 32px;

	a {
		color: inherit;
		text-decoration: none;

		&:hover {
			color: var(--v-anchor-base);
			text-decoration: underline;
		}
	}

	.v-list-item {
		margin-bottom: 8px;
	}
}

.c-QuickSearchResults__albumSeries {
	margin-top: 8px;
	font-size: 85%;
	color: var(--dem-text-lighter);
}
</style>
