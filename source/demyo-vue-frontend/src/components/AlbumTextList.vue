<template>
	<div class="c-AlbumTextList">
		<v-list>
			<template v-for="(value, key) in paginatedAlbumsBySeries">
				<v-list-group
					v-if="value.isSeries"
					:key="key"
					:value="false"
					no-action
					sub-group
				>
					<template #activator>
						<v-list-item-content>
							<v-list-item-title v-text="value.identifyingName" />
						</v-list-item-content>
						<v-list-item-action>
							<v-btn :to="`/series/${value.id}/view`" icon>
								<v-icon>mdi-eye</v-icon>
							</v-btn>
						</v-list-item-action>
					</template>

					<v-list-item v-for="album in value.albums" :key="album.id" :to="`/albums/${album.id}/view`">
						<v-list-item-content class="pl-4">
							<v-list-item-title v-text="album.title" />
							<v-list-item-subtitle>
								<slot :album="album" />
							</v-list-item-subtitle>
						</v-list-item-content>
					</v-list-item>
				</v-list-group>

				<v-list-item v-if="!value.isSeries" :key="key" :to="`/albums/view/${value.id}`">
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
			itemsPerPage: 10,
			currentPage: 1
		}
	},

	computed: {
		albumsBySeries() {
			const bySeries = {}
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
				a.series = undefined // Avoid cycle between series and albums
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

	// All selectors below: remove excessive padding in our particular case
	.v-list-group--sub-group .v-list-group__header {
		padding-right: 8px;
		padding-left: 8px;
	}

	.v-application--is-ltr & {
		.v-list-group--no-action.v-list-group--sub-group > .v-list-group__items > .v-list-item {
			padding-left: 32px;
		}
	}

	.v-application--is-rtl & {
		.v-list-group--no-action.v-list-group--sub-group > .v-list-group__items > .v-list-item {
			padding-right: 32px;
		}
	}
}
</style>
