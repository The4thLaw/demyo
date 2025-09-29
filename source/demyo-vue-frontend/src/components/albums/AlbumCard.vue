<template>
	<div>
		<v-skeleton-loader
			v-if="loading" type="card,paragraph,actions"
			class="v-skeleton-loader--variant-outlined"
		/>
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
					icon="mdi-gift" density="compact" variant="outlined"
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
					:label="$t(`field.Album.writers.${album.bookType.labelType}`,
						album.writers ? album.writers.length : 0)"
				>
					<ModelLink :model="album.writers" view="AuthorPseudonym" />
				</FieldValue>
				<FieldValue
					v-if="album.artists && album.artists.length"
					:label="$t(`field.Album.artists.${album.bookType.labelType}`,
						album.artists ? album.artists.length : 0)"
				>
					<ModelLink :model="album.artists" view="AuthorPseudonym" />
				</FieldValue>
				<v-fade-transition>
					<div v-if="expanded">
						<FieldValue
							v-if="album.colorists && album.colorists.length"
							:label="$t('field.Album.colorists', album.colorists ? album.colorists.length : 0)"
						>
							<ModelLink :model="album.colorists" view="AuthorPseudonym" />
						</FieldValue>
						<FieldValue
							v-if="album.inkers && album.inkers.length"
							:label="$t('field.Album.inkers', album.inkers ? album.inkers.length : 0)"
						>
							<ModelLink :model="album.inkers" view="AuthorPseudonym" />
						</FieldValue>
						<FieldValue
							v-if="album.translators && album.translators.length"
							:label="$t('field.Album.translators', album.translators ? album.translators.length : 0)"
						>
							<ModelLink :model="album.translators" view="AuthorPseudonym" />
						</FieldValue>
						<FieldValue
							v-if="album.coverArtists && album.coverArtists.length"
							:label="$t('field.Album.coverArtists', album.coverArtists ? album.coverArtists.length : 0)"
						>
							<ModelLink :model="album.coverArtists" view="AuthorPseudonym" />
						</FieldValue>
						<FieldValue v-if="album.publisher.id" :label="$t('field.Album.publisher', 1)">
							<ModelLink :model="album.publisher" view="PublisherView" />
						</FieldValue>
						<FieldValue v-if="album.collection.id" :label="$t('field.Album.collection')">
							<ModelLink :model="album.collection" view="CollectionView" />
						</FieldValue>
						<FieldValue
							v-if="album.genres && album.genres.length"
							:label="$t('field.Taxonomized.genres', album.genres.length)"
						>
							<TaxonLink :model="album.genres" />
						</FieldValue>
						<FieldValue
							v-if="album.tags && album.tags.length"
							:label="$t('field.Taxonomized.tags', album.tags.length)"
						>
							<TaxonLink :model="album.tags" />
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

<script setup lang="ts">
import { getBaseImageUrl } from '@/helpers/images'
import readerService from '@/services/reader-service'
import { useReaderStore } from '@/stores/reader'
import sortedIndexOf from 'lodash/sortedIndexOf'

const props = withDefaults(defineProps<{
	album: Album
	loading?: boolean
	/**
	 * Whether to load the cover image. On pages with a lot of asynchronous data, loading all covers
	 * can compete with the AJAX calls and make the page feel slow, especially if the thumbnails
	 * aren't generated yet.
	 */
	loadCover?: boolean
}>(), {
	loading: false,
	loadCover: true
})

const expanded = ref(false)
const readingListLoading = ref(false)

const baseImageUrl = computed(() => getBaseImageUrl(props.album.cover))
// eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
const eagerCovers = !(navigator.connection?.saveData)

const readerStore = useReaderStore()
const isInReadingList = computed(() => sortedIndexOf(readerStore.readingList, props.album.id) > -1)

async function markAsRead(): Promise<void> {
	readingListLoading.value = true
	await readerService.removeFromReadingList(props.album.id)
	readingListLoading.value = false
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
</style>
