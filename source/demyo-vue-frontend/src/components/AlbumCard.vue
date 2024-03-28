<template>
	<div>
		<v-card v-if="loading" variant="outlined" class="c-AlbumCard--loading">
			<v-card-title />
			<v-card-text>
				<div class="c-AlbumCard__contentFaker" />
				<div class="c-AlbumCard__contentFaker" />
				<div class="c-AlbumCard__contentFaker" />
				<div class="c-AlbumCard__contentFaker" />
			</v-card-text>
		</v-card>
		<v-card v-else variant="outlined" class="c-AlbumCard">
			<router-link :to="`/albums/${album.id}/view`" class="c-AlbumCard__albumLink">
				<v-img
					v-if="loadCover && album.cover.id"
					:src="`${baseImageUrl}?w=400`"
					:srcset="`
							${baseImageUrl}?w=400 400w,
							${baseImageUrl}?w=700 700w`"
					:eager="eagerCovers"
					aspect-ratio="3"
					gradient="to top, rgba(0, 0, 0, 0.8) 0%, transparent 72px"
					cover
				>
					<v-row align="end" class="fill-height px-4">
						<v-col class="text-h6 text-white">
							{{ album.identifyingName }}
						</v-col>
					</v-row>
				</v-img>
				<v-card-title v-else>
					{{ album.identifyingName }}
				</v-card-title>
			</router-link>
			<v-card-text>
				<v-alert
					v-if="album.wishlist" color="primary" border="start"
					icon="mdi-gift" text density="compact"
				>
					{{ $t('field.Album.wishlist.value.true') }}
				</v-alert>
				<template v-if="album.firstEditionDate && album.firstEditionDate === album.currentEditionDate">
					<FieldValue :label="$t('field.Album.currentEditionDate')">
						{{ $d(new Date(album.firstEditionDate), 'long') }}
						<template v-if="album.markedAsFirstEdition">
							{{ $t('field.Album.markedAsFirstEdition.view') }}
						</template>
						<template v-else>
							{{ $t('field.Album.isFirstEdition') }}
						</template>
					</FieldValue>
				</template>
				<template v-else>
					<FieldValue v-if="album.firstEditionDate" :label="$t('field.Album.firstEditionDate')">
						{{ $d(new Date(album.firstEditionDate), 'long') }}
					</FieldValue>
					<FieldValue v-if="album.currentEditionDate" :label="$t('field.Album.currentEditionDate')">
						{{ $d(new Date(album.currentEditionDate), 'long') }}
					</FieldValue>
				</template>
				<FieldValue
					v-if="album.writers && album.writers.length"
					:label="$t('field.Album.writers', album.writers ? album.writers.length : 0)"
				>
					<ModelLink :model="album.writers" view="AuthorView" />
				</FieldValue>
				<FieldValue
					v-if="album.artists && album.artists.length"
					:label="$t('field.Album.artists', album.artists ? album.artists.length : 0)"
				>
					<ModelLink :model="album.artists" view="AuthorView" />
				</FieldValue>
				<v-fade-transition>
					<div v-if="expanded">
						<FieldValue
							v-if="album.colorists && album.colorists.length"
							:label="$t('field.Album.colorists', album.colorists ? album.colorists.length : 0)"
						>
							<ModelLink :model="album.colorists" view="AuthorView" />
						</FieldValue>
						<FieldValue
							v-if="album.inkers && album.inkers.length"
							:label="$t('field.Album.inkers', album.inkers ? album.inkers.length : 0)"
						>
							<ModelLink :model="album.inkers" view="AuthorView" />
						</FieldValue>
						<FieldValue
							v-if="album.translators && album.translators.length"
							:label="$t('field.Album.translators', album.translators ? album.translators.length : 0)"
						>
							<ModelLink :model="album.translators" view="AuthorView" />
						</FieldValue>
						<FieldValue v-if="album.publisher.id" :label="$t('field.Album.publisher', 1)">
							<ModelLink :model="album.publisher" view="PublisherView" />
						</FieldValue>
						<FieldValue v-if="album.collection.id" :label="$t('field.Album.collection')">
							<ModelLink :model="album.collection" view="CollectionView" />
						</FieldValue>
						<FieldValue v-if="album.tags && album.tags.length" :label="$t('field.Album.tags')">
							<TagLink :model="album.tags" />
						</FieldValue>
						<FieldValue v-if="album.binding.id" :label="$t('field.Album.binding')">
							<ModelLink :model="album.binding" view="BindingView" />
						</FieldValue>
						<FieldValue v-if="album.acquisitionDate" :label="$t('field.Album.acquisitionDate')">
							{{ $d(new Date(album.acquisitionDate), 'long') }}
						</FieldValue>
					</div>
				</v-fade-transition>
			</v-card-text>
			<v-card-actions>
				<v-btn v-if="!expanded" variant="text" color="secondary" @click="expanded = true">
					{{ $t('page.Series.albums.viewMore') }}
				</v-btn>
				<v-btn v-if="expanded" variant="text" color="secondary" @click="expanded = false">
					{{ $t('page.Series.albums.viewLess') }}
				</v-btn>
				<v-spacer />
				<v-btn v-if="isInReadingList" :loading="readingListLoading" icon @click="markAsRead">
					<v-icon>mdi-library</v-icon>
				</v-btn>
				<FavouriteButton :variant="'text'" :color="null" :model-id="album.id" type="Album" />
				<!-- Eventually, replace the following button with an overflow menu to edit, change reading list,
				change wishlist, delete if the album can be deleted (no derivatives)... -->
				<v-btn :to="`/albums/${album.id}/edit`" icon>
					<v-icon>mdi-pencil</v-icon>
				</v-btn>
			</v-card-actions>
		</v-card>
	</div>
</template>

<script>
import FavouriteButton from '@/components/FavouriteButton.vue'
import FieldValue from '@/components/FieldValue.vue'
import ModelLink from '@/components/ModelLink.vue'
import TagLink from '@/components/TagLink.vue'
import { getBaseImageUrl } from '@/helpers/images'
import readerService from '@/services/reader-service'
import { useReaderStore } from '@/stores/reader'
import sortedIndexOf from 'lodash/sortedIndexOf'
import { mapState } from 'pinia'

export default {
	name: 'AlbumCard',

	components: {
		FavouriteButton,
		FieldValue,
		ModelLink,
		TagLink
	},

	props: {
		album: {
			type: null,
			required: true
		},

		loading: {
			type: Boolean,
			default: false
		},

		/**
		 * Whether to load the cover image. On pages with a lot of asynchronous data, loading all covers
		 * can compete with the AJAX calls and make the page feel slow, especially if the thumbnails
		 * aren't generated yet.
		 */
		loadCover: {
			type: Boolean,
			default: true
		}
	},

	data() {
		return {
			expanded: false,
			readingListLoading: false
		}
	},

	computed: {
		baseImageUrl() {
			return getBaseImageUrl(this.album.cover)
		},

		eagerCovers() {
			return !(navigator.connection?.saveData)
		},

		...mapState(useReaderStore, {
			readingList: store => store.readingList
		}),

		isInReadingList() {
			return sortedIndexOf(this.readingList, this.album.id) > -1
		}
	},

	methods: {
		async markAsRead() {
			this.readingListLoading = true
			await readerService.removeFromReadingList(this.album.id)
			this.readingListLoading = false
		}
	}
}
</script>

<style lang="scss">
.c-AlbumCard__contentFaker {
	min-height: 1.75em;
	margin-top: 1em;
	margin-bottom: 1em;
	border-radius: 8px;
}

.c-AlbumCard {
	.v-card-title {
		background-color: rgb(var(--v-theme-primary));
		color: rgb(var(--v-theme-on-primary));
	}

	&__albumLink {
		&:hover {
			text-decoration: none !important;
		}

		& .dem-theme--light.v-image,
		& .dem-theme--dark.v-image {
			color: white;
		}
	}

	.v-responsive__content .row {
		margin: 0;
	}
}

.c-AlbumCard--loading {
	.v-card-title {
		min-height: 2.5em;
	}

	.v-card-title,
	.c-AlbumCard__contentFaker {
		background-color: #bbb;
		animation: 1.5s ease-in-out 0s infinite alternate pulsate;

		@media (prefers-reduced-motion: reduce) {
			animation: none;
		}
	}

	.c-AlbumCard__contentFaker:nth-child(1) {
		max-width: 90%;
	}

	.c-AlbumCard__contentFaker:nth-child(2) {
		max-width: 70%;
	}

	.c-AlbumCard__contentFaker:nth-child(3) {
		max-width: 80%;
	}

	.c-AlbumCard__contentFaker:nth-child(4) {
		max-width: 95%;
	}
}

@keyframes pulsate {
	/*
	 * Animating opacity is much, much more efficient than animating the background color,
	 * which requires repaints. See
	 * https://developer.mozilla.org/en-US/docs/Tools/Performance/Scenarios/Animating_CSS_properties#CSS_property_cost
	 */
	from {
		opacity: 1;
	}

	to {
		opacity: 0.5;
	}
}
</style>
