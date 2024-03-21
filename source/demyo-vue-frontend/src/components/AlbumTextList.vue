<template>
	<div class="c-AlbumTextList">
		<v-list>
			<template v-for="(value, key) in paginatedAlbumsBySeries">
				<v-list-group
					v-if="value.isSeries"
					:key="key"
					color="primary"
				>
					<template #activator="{ props }">
						<v-list-item v-bind="props" :title="value.identifyingName">
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
			total-visible="10"
			class="my-2"
		/>
	</div>
</template>

<script>
export default {
	name: 'AlbumTextList',

	props: {
		albums: {
			type: Array,
			required: true
		}
	},

	data() {
		return {
			itemsPerPage: 10,
			currentPage: 1
		}
	},

	computed: {
		albumsBySeries() {
			const bySeries = {}
			// Work on a copy of the albums, else we modify the data from the parent

			this.albums.forEach(a => {
				let id
				if (a.series) {
					id = a.series.id
				} else {
					id = -a.id // Negative for albums, positive for series
				}
				if (bySeries[id]) {
					bySeries[id].albums.push(a)
				} else {
					if (id > 0) {
						bySeries[id] = a.series
						bySeries[id].isSeries = true
						bySeries[id].albums = [a]
						bySeries[id].sortName = a.series.identifyingName
					} else {
						bySeries[id] = a
						bySeries[id].isSeries = false
						bySeries[id].sortName = a.title
					}
				}
			})

			// Albums within a Series are already sorted, and Series are sorted, but one shots aren't
			const ret = Object.values(bySeries)
			ret.sort((a, b) => {
				return a.sortName.localeCompare(b.sortName)
			})

			return ret
		},

		paginatedAlbumsBySeries() {
			return this.albumsBySeries.slice((this.currentPage - 1) * this.itemsPerPage,
				this.currentPage * this.itemsPerPage)
		},

		pageCount() {
			return Math.ceil(this.albumsBySeries.length / this.itemsPerPage)
		}
	}
}
</script>

<style lang="less">
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
