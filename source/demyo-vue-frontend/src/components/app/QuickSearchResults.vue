<template>
	<SectionCard :loading="loading" class="c-QuickSearchResults">
		<template v-if="hasResults">
			<template v-for="key in modelTypes">
				<template v-if="results[key]">
					<h2 :key="`ti-${key}-title`" class="text-subtitle-1 text-primary">
						{{ $t('quicksearch.results.title.' + key) }}
					</h2>
					<v-list :key="`ti-${key}`" class="dem-columnized c-QuickSearchResults__list" density="compact">
						<v-list-item v-for="item in results[key]" :key="item.id">
							<div>
								<router-link :to="`/${key}/${item.id}/view`" @click="emit('navigate')">
									<template v-if="key === 'albums'">
										{{ (item as Album).title }}
									</template>
									<template v-else-if="key === 'authors'">
										{{ (item as Author).nameWithPseudonym }}
									</template>
									<template v-else>
										{{ item.identifyingName }}
									</template>
								</router-link>
							</div>
							<div
								v-if="key === 'albums' && (item as Album).series"
								class="c-QuickSearchResults__albumSeries"
							>
								<router-link
									:to="`/series/${(item as Album).series.id}/view`" @click="emit('navigate')"
								>
									{{ (item as Album).series.identifyingName }}
								</router-link>
							</div>
						</v-list-item>
					</v-list>
				</template>
			</template>
		</template>
		<template v-else-if="hasLoadedResults">
			<v-alert border="start" type="info" class="my-4" variant="outlined">
				{{ $t('quicksearch.noResults') }}
			</v-alert>
		</template>
	</SectionCard>
</template>

<script setup lang="ts">
const modelTypes = ['universes', 'series', 'albums', 'tags', 'authors', 'publishers', 'collections']

const props = withDefaults(defineProps<{
	results?: Record<string, IModel[]>,
	loading?: boolean
}>(), {
	results: undefined,
	loading: false
})

const emit = defineEmits(['navigate'])

const hasLoadedResults = computed(() => props.results !== undefined)
const hasResults = computed(() => props.results && Object.keys(props.results).length > 0)
</script>

<style lang="scss">
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
			color: rgb(var(--v-theme-secondary));
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
