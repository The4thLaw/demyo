<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Album.identification')">
				<v-row>
					<v-col cols="12">
						<AutoComplete
							v-model="album.series.id" :items="allSeries" label-key="field.Album.series"
							clearable
						/>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.cycle" :label="$t('field.Album.cycle')"
							type="number" inputmode="decimal" step="any" :rules="rules.cycle"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.number" :label="$t('field.Album.number')"
							type="number" inputmode="decimal" step="any" :rules="rules.number"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field v-model="album.numberSuffix" :label="$t('field.Album.numberSuffix')" />
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="album.title" :label="$t('field.Album.title')" required
							:rules="rules.title"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field v-model="album.originalTitle" :label="$t('field.Album.originalTitle')" />
					</v-col>
					<v-col cols="12">
						<AutoComplete
							v-model="album.tags" :items="allTags" :loading="allTagsLoading"
							multiple clearable
							label-key="field.Album.tags" refreshable @refresh="refreshTags"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.publishing')">
				<v-row>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.publisher.id" :items="allPublishers"
							label-key="field.Album.publisher" required :rules="rules.publisher"
							refreshable @refresh="refreshPublishers" @input="loadCollections"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.collection.id" :items="relatedCollections"
							label-key="field.Album.collection" clearable
							refreshable @refresh="loadCollections"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.authoring')">
				<v-row>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.writers" :items="allAuthors" label-key="field.Album.writers"
							multiple refreshable @refresh="refreshAuthors"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.artists" :items="allAuthors" label-key="field.Album.artists"
							multiple refreshable @refresh="refreshAuthors"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.colorists" :items="allAuthors" label-key="field.Album.colorists"
							multiple refreshable @refresh="refreshAuthors"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.inkers" :items="allAuthors" label-key="field.Album.inkers"
							multiple refreshable @refresh="refreshAuthors"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.translators" :items="allAuthors" label-key="field.Album.translators"
							multiple refreshable @refresh="refreshAuthors"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('page.Album.aboutEdition')">
				<v-row>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.firstEditionDate" :label="$t('field.Album.firstEditionDate')"
							type="date" @change="adjustEditionDates"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.currentEditionDate" :label="$t('field.Album.currentEditionDate')"
							type="date" :readonly="sameEditionDates"
							:append-icon="sameEditionDates ? 'mdi-pencil-off-outline' : ''"
						/>
						<v-checkbox
							v-model="sameEditionDates" :label="$t('field.Album.currentEditionDate.sameAsFirst')"
							:readonly="album.markedAsFirstEdition" @update:modelValue="adjustEditionDates"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-checkbox
							v-model="album.markedAsFirstEdition" :label="$t('field.Album.markedAsFirstEdition.edit')"
							@update:modelValue="adjustEditionDates"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.printingDate" :label="$t('field.Album.printingDate')" type="date"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.isbn" :label="$t('field.Album.isbn')" :rules="rules.isbn"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.format')">
				<v-row>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.binding.id" :items="allBindings"
							label-key="field.Album.binding" clearable
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field v-model="album.location" :label="$t('field.Album.location')" />
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.width" :label="$t('field.Album.width')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.height" :label="$t('field.Album.height')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.pages" :label="$t('field.Album.pages')"
							type="number" inputmode="decimal"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.acquisition')">
				<v-row>
					<v-col cols="12" md="4">
						<v-checkbox v-model="album.wishlist" :label="$t('field.Album.wishlist.edit')" />
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="album.acquisitionDate" :label="$t('field.Album.acquisitionDate')"
							type="date" :disabled="album.wishlist"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<CurrencyField
							v-model="album.purchasePrice" :disabled="album.wishlist"
							label-key="field.Album.purchasePrice"
						/>
					</v-col>
					<PriceManagement v-model="album" model-name="Album" cols="12" md="6" />
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.freeText')">
				<v-row>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">{{ $t('field.Album.summary') }}</label>
						<!--
						TODO: Vue 3: restore RTE
						<tiptap-vuetify
							v-model="album.summary" :extensions="tipTapExtensions"
							:card-props="{ outlined: true }"
						/>
						-->
					</v-col>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">{{ $t('field.Album.comment') }}</label>
						<!--
						TODO: Vue 3: restore RTE

						<tiptap-vuetify
							v-model="album.comment" :extensions="tipTapExtensions"
							:card-props="{ outlined: true }"
						/>
						-->
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Album.images')">
				<v-row>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.cover.id" :items="allImages" :loading="allImagesLoading"
							label-key="field.Album.cover" refreshable @refresh="refreshImages"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="album.images" :items="allImages" :loading="allImagesLoading"
							:multiple="true"
							label-key="field.Album.images" refreshable @refresh="refreshImages"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<FormActions v-if="initialized" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script>
import CurrencyField from '@/components/CurrencyField.vue'
import FormActions from '@/components/FormActions.vue'
import PriceManagement from '@/components/PriceManagement.vue'
import SectionCard from '@/components/SectionCard.vue'
import { tipTapExtensions } from '@/helpers/fields'
import { integer, isbn, mandatory, number } from '@/helpers/rules'
import modelEditMixin from '@/mixins/model-edit'
import authorRefreshMixin from '@/mixins/refresh-author-list'
import imgRefreshMixin from '@/mixins/refresh-image-list'
import publisherRefreshMixin from '@/mixins/refresh-publisher-list'
import tagRefreshMixin from '@/mixins/refresh-tag-list'
import albumService from '@/services/album-service'
import bindingService from '@/services/binding-service'
import publisherService from '@/services/publisher-service'
import seriesService from '@/services/series-service'

export default {
	name: 'AlbumEdit',

	components: {
		CurrencyField,
		FormActions,
		PriceManagement,
		SectionCard
	},

	mixins: [modelEditMixin, authorRefreshMixin, imgRefreshMixin, publisherRefreshMixin, tagRefreshMixin],

	data() {
		return {
			mixinConfig: {
				modelEdit: {
					titleKeys: {
						add: 'title.add.album',
						edit: 'title.edit.album'
					},
					saveRedirectViewName: 'AlbumView'
				}
			},

			allSeries: [],
			allBindings: [],
			relatedCollectionsLoading: false,
			relatedCollections: [],
			album: {
				series: {},
				writers: [],
				artists: [],
				colorists: [],
				inkers: [],
				translators: [],
				publisher: {},
				collection: {},
				binding: {},
				prices: [],
				cover: {}
			},
			sameEditionDates: false,

			tipTapExtensions: tipTapExtensions,

			rules: {
				cycle: [integer()],
				number: [number()],
				title: [mandatory()],
				publisher: [mandatory()],
				isbn: [isbn()]
			}
		}
	},

	methods: {
		adjustEditionDates() {
			if (this.album.markedAsFirstEdition) {
				this.sameEditionDates = true
			}
			if (this.sameEditionDates) {
				this.album.currentEditionDate = this.album.firstEditionDate
			}
		},

		async fetchData() {
			if (this.parsedId) {
				this.album = await albumService.findById(this.parsedId)
			}

			if (!this.parsedId && this.$route.query.toSeries) {
				this.album = await seriesService.getAlbumTemplate(parseInt(this.$route.query.toSeries, 10))
			}

			// Find all reference data
			const pSeries = seriesService.findForList()
			const pBindings = bindingService.findForList()

			// Load collections with the currently available data
			void this.loadCollections()

			// Check the edition dates
			this.sameEditionDates = this.album.firstEditionDate
				&& this.album.firstEditionDate === this.album.currentEditionDate

			// Assign all reference data
			this.allBindings = await pBindings
			this.allSeries = await pSeries
		},

		async loadCollections() {
			this.relatedCollectionsLoading = true
			this.relatedCollections = await publisherService.findCollectionsForList(this.album.publisher.id)
			if (this.album.collection.id) {
				// If the current album ID is not in the returned list, clear it
				if (!this.relatedCollections.find(val => val.id === this.album.collection.id)) {
					this.album.collection.id = undefined
				}
			}
			this.relatedCollectionsLoading = false
		},

		saveHandler() {
			return albumService.save(this.album)
		}
	}
}
</script>
