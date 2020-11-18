<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Album.identification')">
				<v-row>
					<v-col cols="12">
						<Autocomplete
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
					<v-col cols="12">
						<v-text-field
							v-model="album.title" :label="$t('field.Album.title')" required
							:rules="rules.title"
						/>
					</v-col>
					<v-col cols="12">
						<Autocomplete
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
						<Autocomplete
							v-model="album.publisher.id" :items="allPublishers"
							label-key="field.Album.publisher" required :rules="rules.publisher"
							refreshable @refresh="refreshPublishers" @input="loadCollections"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="album.collection.id" :items="relatedCollections"
							label-key="field.Album.collection" clearable
							refreshable @refresh="loadCollections"
						/>
					</v-col>
				</v-row>
			</SectionCard>
			<!-- TODO: ISBN -->

			<SectionCard :subtitle="$t('fieldset.Album.authoring')">
				<v-row>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="album.writers" :items="allAuthors" label-key="field.Album.writers"
							multiple refreshable @refresh="refreshAuthors"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="album.artists" :items="allAuthors" label-key="field.Album.artists"
							multiple refreshable @refresh="refreshAuthors"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="album.colorists" :items="allAuthors" label-key="field.Album.colorists"
							multiple refreshable @refresh="refreshAuthors"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="album.inkers" :items="allAuthors" label-key="field.Album.inkers"
							multiple refreshable @refresh="refreshAuthors"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="album.translators" :items="allAuthors" label-key="field.Album.translators"
							multiple refreshable @refresh="refreshAuthors"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('page.Album.aboutEdition')">
				<v-col cols="12">
					<v-text-field
						v-model="album.isbn" :label="$t('field.Album.isbn')" :rules="rules.isbn"
					/>
				</v-col>
			</SectionCard>

			<!--<SectionCard :subtitle="$t('fieldset.Derivative.format')">
				<v-row>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="derivative.type.id" :items="allTypes"
							label-key="field.Derivative.type" :rules="rules.type"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="derivative.colours" :label="$t('field.Derivative.colours')"
							:rules="rules.colours" type="number" inputmode="numeric"
						/>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="derivative.width" :label="$t('field.Derivative.width')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="derivative.height" :label="$t('field.Derivative.height')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="derivative.depth" :label="$t('field.Derivative.depth')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Derivative.description')">
				<v-row>
					<v-col cols="12" sm="6" lg="2">
						<v-text-field
							v-model="derivative.number" :label="$t('field.Derivative.number')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
					<v-col cols="12" sm="6" lg="2">
						<v-text-field
							v-model="derivative.total" :label="$t('field.Derivative.total')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
					<v-col cols="12" sm="6" md="4" lg="2">
						<v-checkbox v-model="derivative.signed" :label="$t('field.Derivative.signed.edit')" />
					</v-col>
					<v-col cols="12" sm="6" md="4" lg="3">
						<v-checkbox v-model="derivative.authorsCopy" :label="$t('field.Derivative.authorsCopy.edit')" />
					</v-col>
					<v-col cols="12" sm="6" md="4" lg="3">
						<v-checkbox
							v-model="derivative.restrictedSale" :label="$t('field.Derivative.restrictedSale.edit')"
						/>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">{{ $t('field.Derivative.description') }}</label>
						<tiptap-vuetify
							v-model="derivative.description" :extensions="tipTapExtensions"
							:card-props="{ outlined: true }"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="derivative.images" :items="allImages" :loading="allImagesLoading"
							:multiple="true"
							label-key="field.Derivative.images" refreshable @refresh="refreshImages"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Derivative.acquisition')">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="derivative.acquisitionDate" :label="$t('field.Derivative.acquisitionDate')"
							type="date"
						/>
						<v-text-field
							v-model="derivative.purchasePrice" :label="$t('field.Derivative.purchasePrice')"
							type="number" inputmode="decimal" step="any"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">{{ $t('field.Derivative.prices.history') }}</label>
						-->
						<!-- Note: keyed by index, which is not ideal,
						because the price doesn't have a technical ID -->
						<!--<v-row
							v-for="(price, index) in derivative.prices" :key="'price_' + index"
							dense class="v-DerivativeEdit__priceRow"
						>
							<v-col cols="12" md="6">
								<v-text-field
									v-model="price.date" :label="$t('field.Derivative.prices.date')"
									type="date" :rules="rules.prices.date" required
								/>
							</v-col>
							<v-col cols="12" md="6">
								<v-text-field
									v-model="price.price" :label="$t('field.Derivative.prices.price')"
									type="number" inputmode="decimal" step="any" :rules="rules.prices.price"
									required
								/>
								<v-btn icon @click="removePrice(index)">
									<v-icon>mdi-minus</v-icon>
								</v-btn>
							</v-col>
						</v-row>
						<div class="v-DerivativeEdit__priceAdder">
							<v-btn icon @click="addPrice">
								<v-icon>mdi-plus</v-icon>
							</v-btn>
						</div>
					</v-col>
				</v-row>
			</SectionCard>-->

			<FormActions v-if="initialized" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script>
import { TiptapVuetify } from 'tiptap-vuetify'
import Autocomplete from '@/components/Autocomplete'
import FormActions from '@/components/FormActions'
import SectionCard from '@/components/SectionCard'
import { tipTapExtensions } from '@/helpers/fields'
import modelEditMixin from '@/mixins/model-edit'
import authorRefreshMixin from '@/mixins/refresh-author-list'
import imgRefreshMixin from '@/mixins/refresh-image-list'
import publisherRefreshMixin from '@/mixins/refresh-publisher-list'
import tagRefreshMixin from '@/mixins/refresh-tag-list'
import { integer, isbn, mandatory, number } from '@/helpers/rules'
import albumService from '@/services/album-service'
import bindingService from '@/services/binding-service'
import publisherService from '@/services/publisher-service'
import seriesService from '@/services/series-service'

export default {
	name: 'AlbumEdit',

	components: {
		Autocomplete,
		FormActions,
		SectionCard,
		TiptapVuetify
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
				binding: {},
				prices: []
			},

			tipTapExtensions: tipTapExtensions,

			rules: {
				cycle: [integer(this)],
				number: [number(this)],
				title: [mandatory(this)],
				publisher: [mandatory(this)],
				isbn: [isbn(this)],
				prices: {
					date: [mandatory(this)],
					price: [mandatory(this)]
				}
			}
		}
	},

	methods: {
		addPrice() {
			const newPrice = {
				date: null,
				price: null
			}
			this.derivative.prices.push(newPrice)
		},

		removePrice(index) {
			this.derivative.prices.splice(index, 1)
		},

		async fetchData() {
			if (this.parsedId) {
				this.album = await albumService.findById(this.parsedId)
			}

			if (!this.parsedId && this.$route.query.toSeries) {
				// TODO: load a template like we used to do
				this.album.series.id = parseInt(this.$route.query.toSeries, 10)
			}

			// Find all reference data
			const pSeries = seriesService.findForList()
			const pBindings = bindingService.findForList()

			// Load collections with the currently available data
			this.loadCollections()

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

<style lang="less">
// TODO: make this common with derivatives. Perhaps extract the price management to a component
.v-AlbumEdit__priceRow {
	> :last-child {
		display: flex;
		align-items: center;

		> button {
			margin-left: 1em;
		}
	}
}

.v-AlbumEdit__priceAdder {
	text-align: right;
}
</style>
