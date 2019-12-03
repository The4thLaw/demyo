<template>
	<div>
		Hello, albums -{{ albumsBySeries.length }}
		<v-list>
			<template v-for="(value, key) in paginatedAlbumsBySeries">
				<v-list-group v-if="value.isSeries" :key="key" sub-group :value="value.albums.length < albumsPerSeriesThreshold">
					<template v-slot:activator>
						<v-list-item-content>
							<v-list-item-title v-text="value.name" />
						</v-list-item-content>
					</template>

					<v-list-item v-for="album in value.albums" :key="album.id" :to="`/albums/view/${album.id}`">
						<v-list-item-content class="pl-4">
							<v-list-item-title v-text="album.title" />
							<v-list-item-subtitle>
								<slot :album="album" />
							</v-list-item-subtitle>
						</v-list-item-content>
					</v-list-item>
				</v-list-group>

				<v-list-item v-if="!value.isSeries" to="/" :key="key">
					<v-list-item-content>
						<v-list-item-title>{{ value.title }}</v-list-item-title>
						<v-list-item-subtitle>
							<slot :album="value" />
						</v-list-item-subtitle>
					</v-list-item-content>
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
			albumsPerSeriesThreshold: 3,
			itemsPerPage: 10,
			currentPage: 1
		}
	},

	computed: {
		hasDefaultSlot() {
			return !!this.$scopedSlots.default
		},

		albumsBySeries() {
			let bySeries = {}
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
						bySeries[id].albums = [ a ]
						bySeries[id].sortName = a.series.name
					} else {
						bySeries[id] = a
						bySeries[id].isSeries = false
						bySeries[id].sortName = a.title
					}
				}
				a.series = undefined // Avoid cycle between series and albums
			})

			// Albums within a Series are already sorted, and Series are sorted, but one shots aren't
			let ret = Object.values(bySeries)
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
