<template>
	<div>
		<v-card v-if="loading" outlined class="c-AlbumCard--loading">
			<v-card-title />
			<v-card-text>
				<div class="c-AlbumCard__contentFaker" />
				<div class="c-AlbumCard__contentFaker" />
				<div class="c-AlbumCard__contentFaker" />
				<div class="c-AlbumCard__contentFaker" />
			</v-card-text>
		</v-card>
		<v-card v-else outlined class="c-AlbumCard">
			<router-link :to="`/albums/${album.id}/view`" class="c-AlbumCard__albumLink">
				<v-card-title>
					{{ album.identifyingName }}
				</v-card-title>
			</router-link>
			<v-card-text>
				<template v-if="album.firstEditionDate && album.firstEditionDate === album.currentEditionDate">
					<FieldValue :label="$t('field.Album.firstEditionDate')">
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
					v-if="album.writers"
					:label="$tc('field.Album.writers', album.writers ? album.writers.length : 0)"
				>
					<ModelLink :model="album.writers" view="AuthorView" />
				</FieldValue>
				<FieldValue
					v-if="album.artists"
					:label="$tc('field.Album.artists', album.artists ? album.artists.length : 0)"
				>
					<ModelLink :model="album.artists" view="AuthorView" />
				</FieldValue>
				<v-fade-transition>
					<div v-if="expanded">
						<FieldValue
							v-if="album.colorists"
							:label="$tc('field.Album.colorists', album.colorists ? album.colorists.length : 0)"
						>
							<ModelLink :model="album.colorists" view="AuthorView" />
						</FieldValue>
						<FieldValue
							v-if="album.inkers"
							:label="$tc('field.Album.inkers', album.inkers ? album.inkers.length : 0)"
						>
							<ModelLink :model="album.inkers" view="AuthorView" />
						</FieldValue>
						<FieldValue
							v-if="album.translators"
							:label="$tc('field.Album.translators', album.translators ? album.translators.length : 0)"
						>
							<ModelLink :model="album.translators" view="AuthorView" />
						</FieldValue>
						<FieldValue v-if="album.publisher" :label="$t('field.Album.publisher')">
							<ModelLink :model="album.publisher" view="PublisherView" />
						</FieldValue>
						<FieldValue v-if="album.collection" :label="$t('field.Album.collection')">
							<ModelLink :model="album.collection" view="CollectionView" />
						</FieldValue>
						<FieldValue v-if="album.tags" :label="$t('field.Album.tags')">
							<TagLink :model="album.tags" />
						</FieldValue>
						<FieldValue v-if="album.binding" :label="$t('field.Album.binding')">
							<ModelLink :model="album.binding" view="BindingView" />
						</FieldValue>
						<FieldValue v-if="album.acquisitionDate" :label="$t('field.Album.acquisitionDate')">
							{{ $d(new Date(album.acquisitionDate), 'long') }}
						</FieldValue>
					</div>
				</v-fade-transition>
			</v-card-text>
			<v-card-actions>
				<v-btn v-if="!expanded" text color="accent" @click="expanded = true">
					{{ $t('page.Series.albums.viewMore') }}
				</v-btn>
				<v-btn v-if="expanded" text color="accent" @click="expanded = false">
					{{ $t('page.Series.albums.viewLess') }}
				</v-btn>
				<v-spacer />
				<v-btn icon @click="album.favorite = !album.favorite">
					<!-- TODO: check if fave, set/unset -->
					<v-icon v-if="album.favorite" color="accent">mdi-heart</v-icon>
					<v-icon v-if="!album.favorite">mdi-heart-outline</v-icon>
				</v-btn>
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
import FieldValue from '@/components/FieldValue'
import ModelLink from '@/components/ModelLink'
import TagLink from '@/components/TagLink'

export default {
	name: 'AlbumCard',

	components: {
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
		}
	},

	data() {
		return {
			expanded: false
		}
	}
}
</script>

<style lang="less">
// TODO[dark] adapt load colors to dark theme

.c-AlbumCard__contentFaker {
	min-height: 1.75em;
	margin-top: 1em;
	margin-bottom: 1em;
	border-radius: 8px;
}

.c-AlbumCard {
	.v-card__title {
		background-color: var(--v-primary-base);
		color: var(--dem-primary-contrast);
	}

	&.theme--light.v-card > .v-card__text {
		color: var(--dem-text);
	}

	&__albumLink:hover {
		text-decoration: none !important;
	}
}

.c-AlbumCard--loading {
	.v-card__title {
		min-height: 2.5em;
	}

	.v-card__title,
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
