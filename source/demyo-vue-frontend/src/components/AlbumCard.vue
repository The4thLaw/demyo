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
					<FieldValue :label="$t('field.Album.firstEditionDate')" :value="true">
						<!--{{ $d(new Date(album.firstEditionDate), 'long') }}-->
						<template v-if="album.markedAsFirstEdition">
							{{ $t('field.Album.markedAsFirstEdition.view') }}
						</template>
						<template v-else>
							{{ $t('field.Album.isFirstEdition') }}
						</template>
					</FieldValue>
				</template>
				<template v-else>
					<FieldValue :label="$t('field.Album.firstEditionDate')" :value="album.firstEditionDate">
						<!--{{ $d(new Date(album.firstEditionDate), 'long') }}-->
					</FieldValue>
					<FieldValue :label="$t('field.Album.currentEditionDate')" :value="album.currentEditionDate">
						<!--{{ $d(new Date(album.currentEditionDate), 'long') }}-->
					</FieldValue>
				</template>
				<FieldValue
					:label="$tc('field.Album.writers', album.writers ? album.writers.length : 0)"
					:value="album.writers"
				>
					<ModelLink :model="album.writers" view="AuthorView" />
				</FieldValue>
				<FieldValue
					:label="$tc('field.Album.artists', album.artists ? album.artists.length : 0)"
					:value="album.artists"
				>
					<ModelLink :model="album.artists" view="AuthorView" />
				</FieldValue>
				<v-fade-transition>
					<div v-if="expanded">
						<FieldValue
							:label="$tc('field.Album.colorists', album.colorists ? album.colorists.length : 0)"
							:value="album.colorists"
						>
							<ModelLink :model="album.colorists" view="AuthorView" />
						</FieldValue>
						<FieldValue
							:label="$tc('field.Album.inkers', album.inkers ? album.inkers.length : 0)"
							:value="album.inkers"
						>
							<ModelLink :model="album.inkers" view="AuthorView" />
						</FieldValue>
						<FieldValue
							:label="$tc('field.Album.translators', album.translators ? album.translators.length : 0)"
							:value="album.translators"
						>
							<ModelLink :model="album.translators" view="AuthorView" />
						</FieldValue>
						<FieldValue :label="$t('field.Album.publisher')" :value="album.publisher">
							<ModelLink :model="album.publisher" view="PublisherView" />
						</FieldValue>
						<FieldValue :label="$t('field.Album.collection')" :value="album.collection">
							<ModelLink :model="album.collection" view="CollectionView" />
						</FieldValue>
						<!-- TODO: special component for tags -->
						<FieldValue :label="$t('field.Album.tags')" :value="album.tags">
							<ModelLink :model="album.tags" view="TagView" />
						</FieldValue>
						<FieldValue :label="$t('field.Album.binding')" :value="album.binding">
							<ModelLink :model="album.binding" view="BindingView" />
						</FieldValue>
						<!-- TODO: get rid of FieldValue.value : v-if is more suitable because else the slot is always rendered -->
						<FieldValue :label="$t('field.Album.acquisitionDate')" v-if="album.acquisitionDate" :value="album.acquisitionDate">
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
				<v-btn icon>
					<v-icon>mdi-dots-vertical</v-icon>
					<!-- TODO: menu to edit, toggle reading list, wishlist... Not delete -->
				</v-btn>
			</v-card-actions>
		</v-card>
	</div>
</template>

<script>
import FieldValue from '@/components/FieldValue'
import ModelLink from '@/components/ModelLink'

export default {
	name: 'AlbumCard',

	components: {
		FieldValue,
		ModelLink
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
