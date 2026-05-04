<template>
	<div class="c-AlbumTextList">
		<v-list>
			<template v-for="(value, key) in paginatedAlbumsBySeries">
				<v-list-group
					v-if="value.isSeries"
					:key="key"
					color="primary"
				>
					<template #activator="{ props: templateProps }">
						<v-list-item v-bind="templateProps" :title="value.identifyingName">
							<template #append="appendProps">
								<v-icon v-if="appendProps.isActive">
									mdi-chevron-up
								</v-icon>
								<v-icon v-else>
									mdi-chevron-down
								</v-icon>
								<v-btn :to="`/series/${value.id}/view`" icon size="small" variant="flat">
									<v-icon>mdi-eye</v-icon>
								</v-btn>
							</template>
						</v-list-item>
					</template>

					<v-list-item
						v-for="album in value.albums" :key="album.id" :to="`/albums/${album.id}/view`"
						:title="album.title"
					>
						<template #subtitle>
							<slot :album="album" />
						</template>
					</v-list-item>
				</v-list-group>

				<v-list-item
					v-if="!value.isSeries" :key="key" :to="`/albums/view/${value.id}`"
					:title="value.title"
				>
					<template #subtitle>
						<slot :album="value" />
					</template>
				</v-list-item>
			</template>
		</v-list>

		<v-pagination
			v-if="pageCount > 1"
			v-model="currentPage"
			:length="pageCount"
			:total-visible="paginationVisible"
			class="my-2"
		/>
	</div>
</template>

<script setup lang="ts">
import { useResponsivePageCount } from '@/composables/pagination'

const ITEMS_PER_PAGE = 10

const props = defineProps<{
	albums: Album[]
}>()

const currentPage = ref(1)
const paginationVisible = useResponsivePageCount()

interface MetaSeries extends Partial<Album>, Partial<Series> {
	isSeries?: boolean
	sortName: string
	albums?: Album[]
}

const albumsBySeries = computed(() => {
	const bySeries = {} as Record<number, MetaSeries>
	// Work on a copy of the albums, else we modify the data from the parent

	props.albums.forEach(a => {
		let id
		if (a.series) {
			id = a.series.id
		} else {
			id = -a.id // Negative for albums, positive for series
		}

		if (bySeries[id]) {
			bySeries[id].albums?.push(a)
		} else if (id > 0) {
			bySeries[id] = {
				...a.series,
				isSeries: true,
				albums: [a],
				sortName: a.series.identifyingName
			}
		} else {
			bySeries[id] = {
				...a,
				isSeries: false,
				sortName: a.title
			}
		}
	})

	// Albums within a Series are already sorted, and Series are sorted, but one shots aren't
	const ret = Object.values(bySeries)
	ret.sort((a, b) => {
		return a.sortName.localeCompare(b.sortName)
	})

	return ret
})

const paginatedAlbumsBySeries = computed(() => {
	return albumsBySeries.value.slice((currentPage.value - 1) * ITEMS_PER_PAGE,
		currentPage.value * ITEMS_PER_PAGE)
})

const pageCount = computed(() => {
	return Math.ceil(albumsBySeries.value.length / ITEMS_PER_PAGE)
})
</script>

<style lang="scss">
.c-AlbumTextList {
	.v-list-item {
		/*
		For some reason the header can overlow.
		I can't understand why and the official example work fine so this fix will have to do.
		*/
		max-width: 100%;
	}

	.v-btn {
		// Use the same opacity as the regular icons
		opacity: var(--v-medium-emphasis-opacity);
	}

	// Remove excessive padding in our particular case
	.v-list-group__items .v-list-item {
		// Must use important because Vuetify does
		padding-inline-start: 32px !important;
	}
}
</style>
