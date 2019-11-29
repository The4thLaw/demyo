<template>
	<div>Hello, albums</div>
</template>

<script>
import { groupBy } from 'lodash'

export default {
	name: 'AlbumTextList',

	props: {
		albums: {
			type: Array,
			required: true
		}
	},

	computed: {
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
					} else {
						bySeries[id] = a
						bySeries[id].isSeries = false
					}
				}
			});
			// TODO: sort this, and paginate it
			return Object.values(bySeries)
		}
	}
}
</script>
