<template>
	<!-- TODO: add to reading list (if not in wishlist) -->
	<!-- TODO: FAB to mark as read -->
	<v-container fluid>
		<portal v-if="!loading" to="appBarAddons">
			<FavouriteButton :model-id="album.id" type="Album" />
		</portal>

		<AppTasks v-if="!loading" v-model="appTasksMenu">
			<AppTask
				:label="$t('quickTasks.edit.album')"
				:to="`/albums/${album.id}/edit`"
				icon="mdi-book-open-variant dem-overlay-edit"
			/>
			<!--
			Adding an @click="appTasksMenu = false" causes the dialog to instantly
			disappear because the AppTask isn't rendered anymore
			-->
			<AppTask
				:label="$t('quickTasks.delete.album')"
				:confirm="$t('quickTasks.delete.album.confirm')"
				icon="mdi-book-open-variant dem-overlay-delete"
				@cancel="appTasksMenu = false"
				@confirm="deleteAlbum"
			/>
			<AppTask
				:label="$t('quickTasks.add.images.to.album')"
				icon="mdi-camera dem-overlay-add"
				@click="appTasksMenu = false; dndDialog = true"
			/>
		</AppTasks>
		<DnDImage
			v-model="dndDialog" main-image-label="field.Album.cover"
			other-images-label="field.Album.images" @save="saveDndImages"
		/>

		<SectionCard :loading="loading" :image="album.cover" :title="album.identifyingName">
			<div class="dem-fieldset">
				<v-row>
					<v-col v-if="album.series.id" cols="12" md="6">
						<FieldValue :label="$t('field.Album.series')">
							<ModelLink :model="album.series" view="SeriesView" />
						</FieldValue>
					</v-col>
					<v-col v-if="album.tags && album.tags.length > 0" cols="12">
						<FieldValue :label="$tc('field.Album.tags', album.tags.length)">
							<TagLink :model="album.tags" />
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div class="dem-fieldset">
				<v-row>
					<v-col v-if="album.publisher.id" cols="12" md="6">
						<FieldValue :label="$tc('field.Album.publisher', 1)">
							<ModelLink :model="album.publisher" view="PublisherView" />
						</FieldValue>
					</v-col>
					<v-col v-if="album.collection.id" cols="12" md="6">
						<FieldValue :label="$t('field.Album.collection')">
							<ModelLink :model="album.collection" view="CollectionView" />
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div v-if="hasAuthors" class="dem-fieldset">
				<v-row>
					<v-col v-if="album.writers && album.writers.length" cols="12" md="6">
						<FieldValue :label="$tc('field.Album.writers', album.writers.length)">
							<ModelLink :model="album.writers" view="AuthorView" />
						</FieldValue>
					</v-col>
					<v-col v-if="album.artists && album.artists.length" cols="12" md="6">
						<FieldValue :label="$tc('field.Album.artists', album.artists.length)">
							<ModelLink :model="album.artists" view="AuthorView" />
						</FieldValue>
					</v-col>
					<v-col v-if="album.colorists && album.colorists.length" cols="12" md="6">
						<FieldValue :label="$tc('field.Album.colorists', album.colorists.length)">
							<ModelLink :model="album.colorists" view="AuthorView" />
						</FieldValue>
					</v-col>
					<v-col v-if="album.inkers && album.inkers.length" cols="12" md="6">
						<FieldValue :label="$tc('field.Album.inkers', album.inkers.length)">
							<ModelLink :model="album.inkers" view="AuthorView" />
						</FieldValue>
					</v-col>
					<v-col v-if="album.translators && album.translators.length" cols="12" md="6">
						<FieldValue :label="$tc('field.Album.translators', album.writers.length)">
							<ModelLink :model="album.translators" view="AuthorView" />
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div v-if="album.aggregatedLocation" class="dem-fieldset">
				<FieldValue :label="$t('field.Album.location')">
					{{ album.aggregatedLocation }}
				</FieldValue>
			</div>
		</SectionCard>

		<SectionCard :subtitle="$t('page.Album.aboutEdition')">
			<div
				v-if="album.firstEditionDate || album.currentEditionDate || album.wishlist
					|| album.acquisitionDate || album.isbn"
				class="dem-fieldset"
			>
				<v-row>
					<v-col v-if="album.firstEditionDate" cols="12" md="4">
						<FieldValue :label="$t('field.Album.firstEditionDate')">
							{{ $d(new Date(album.firstEditionDate), 'long') }}
							<template v-if="album.currentEditionDate === album.firstEditionDate">
								{{ $t(album.markedAsFirstEdition ?
									'field.Album.markedAsFirstEdition.view' : 'field.Album.isFirstEdition') }}
							</template>
						</FieldValue>
					</v-col>
					<v-col
						v-if="album.currentEditionDate && album.currentEditionDate !== album.firstEditionDate"
						cols="12" md="4"
					>
						<FieldValue :label="$t('field.Album.currentEditionDate')">
							{{ $d(new Date(album.currentEditionDate), 'long') }}
						</FieldValue>
					</v-col>
					<v-col v-if="album.acquisitionDate" cols="12" md="4">
						<FieldValue :label="$t('field.Album.acquisitionDate')">
							{{ $d(new Date(album.acquisitionDate), 'long') }}
						</FieldValue>
					</v-col>
					<v-col v-if="album.wishlist" cols="12" md="4">
						<FieldValue :label="$t('field.Album.wishlist.view')">
							{{ $t('field.Album.wishlist.value.true') }}
						</FieldValue>
					</v-col>
					<v-col v-if="album.isbn" cols="12" md="4">
						<FieldValue :label="$t('field.Album.isbn')">
							{{ album.isbn }}
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div class="dem-fieldset">
				<v-row>
					<v-col cols="12" md="4">
						<FieldValue :label="$t('field.Album.binding')">
							<ModelLink :model="album.binding" view="BindingView" />
						</FieldValue>
					</v-col>

					<v-col v-if="sizeSpec" cols="12" md="4">
						<FieldValue :label="$t('field.Album.size')">
							{{ sizeSpec }}
						</FieldValue>
					</v-col>

					<template v-if="!sizeSpec">
						<v-col cols="12" md="4">
							<FieldValue v-if="album.height" :label="$t('field.Album.height')">
								{{ album.height }}
							</FieldValue>
						</v-col>
						<v-col cols="12" md="4">
							<FieldValue v-if="album.depth" :label="$t('field.Album.depth')">
								{{ album.depth }}
							</FieldValue>
						</v-col>
					</template>

					<v-col v-if="album.pages" cols="12" md="4">
						<FieldValue :label="$t('field.Album.pages')">
							{{ album.pages }}
						</FieldValue>
					</v-col>
				</v-row>
			</div>

			<div
				v-if="album.purchasePrice || hasPrices"
				class="dem-fieldset"
			>
				<v-row>
					<v-col cols="12" md="6">
						<FieldValue v-if="album.purchasePrice" :label="$t('field.Album.purchasePrice')">
							{{ album.purchasePrice }}
						</FieldValue>
					</v-col>
					<v-col cols="12" md="6">
						<FieldValue v-if="hasPrices" :label="$t('field.Album.prices.history')">
							<v-simple-table>
								<template v-slot:default>
									<thead>
										<tr>
											<th>{{ $t('field.Album.prices.date') }}</th>
											<th>{{ $t('field.Album.prices.price') }}</th>
										</tr>
									</thead>
									<tbody>
										<!-- Note: keyed by index, which is not ideal,
										because the price doesn't have a technical ID -->
										<tr v-for="(price, index) in album.prices" :key="index">
											<td>{{ $d(new Date(price.date), 'long') }}</td>
											<td>{{ price.price }}</td>
										</tr>
									</tbody>
								</template>
							</v-simple-table>
						</FieldValue>
					</v-col>
				</v-row>
			</div>
		</SectionCard>

		<SectionCard v-if="album.summary || album.comment">
			<FieldValue :label="$t('field.Album.summary')">
				<div v-html="album.summary" />
			</FieldValue>
			<FieldValue :label="$t('field.Album.comment')">
				<div v-html="album.comment" />
			</FieldValue>
		</SectionCard>

		<SectionCard v-if="hasImages" :loading="loading" :title="$t('page.Album.gallery')">
			<GalleryIndex :items="album.images">
				<template v-slot:default="slotProps">
					<router-link :to="`/images/${slotProps.item.id}/view`">
						{{ slotProps.item.identifyingName }}
					</router-link>
				</template>
			</GalleryIndex>
		</SectionCard>

		<!-- TODO: list of derivatives -->
	</v-container>
</template>

<script>
import AppTask from '@/components/AppTask'
import AppTasks from '@/components/AppTasks'
import DnDImage from '@/components/DnDImage'
import FavouriteButton from '@/components/FavouriteButton'
import FieldValue from '@/components/FieldValue'
import GalleryIndex from '@/components/GalleryIndex'
import ModelLink from '@/components/ModelLink'
import SectionCard from '@/components/SectionCard'
import TagLink from '@/components/TagLink'
import { deleteStub } from '@/helpers/actions'
import modelViewMixin from '@/mixins/model-view'
import albumService from '@/services/album-service'
import derivativeService from '@/services/derivative-service'

export default {
	name: 'AlbumView',

	components: {
		AppTask,
		AppTasks,
		DnDImage,
		FavouriteButton,
		FieldValue,
		GalleryIndex,
		ModelLink,
		SectionCard,
		TagLink
	},

	mixins: [modelViewMixin],

	metaInfo() {
		return {
			title: this.album.title
		}
	},

	data() {
		return {
			appTasksMenu: false,
			dndDialog: false,
			album: {
				series: {},
				publisher: {},
				collection: {},
				binding: {}
			}
		}
	},

	computed: {
		hasAuthors() {
			return (this.album.writers && this.album.writers.length) ||
				(this.album.artists && this.album.artists.length) ||
				(this.album.colorists && this.album.colorists.length) ||
				(this.album.inkers && this.album.inkers.length) ||
				(this.album.translators && this.album.translators.length)
		},

		hasPrices() {
			return this.album.prices && this.album.prices.length
		},

		hasImages() {
			return this.album.images && this.album.images.length
		},

		sizeSpec() {
			if (this.album.width && this.album.height) {
				return `${this.album.width} x ${this.album.height}`
			}

			return null
		}
	},

	methods: {
		async fetchData() {
			this.album = await albumService.findById(this.parsedId)
		},

		async saveDndImages(data) {
			let ok = await albumService.saveFilepondImages(this.album.id, data.mainImage, data.otherImages)
			if (ok) {
				this.$store.dispatch('ui/showSnackbar', this.$t('draganddrop.snack.confirm'))
				this.fetchDataInternal()
			} else {
				this.$store.dispatch('ui/showSnackbar', this.$t('core.exception.api.title'))
			}
		},

		deleteAlbum() {
			deleteStub(this,
				() => albumService.deleteModel(this.album.id),
				'quickTasks.delete.album.confirm.done',
				'AlbumIndex')
		}
	}
}
</script>
