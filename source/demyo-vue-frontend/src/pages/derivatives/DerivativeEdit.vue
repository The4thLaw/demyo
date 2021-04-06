<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Derivative.origin')">
				<v-row>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="derivative.series.id" :items="allSeries" label-key="field.Derivative.series"
							clearable :rules="rules.albumOrSeries" @input="loadAlbums(); $refs.form.validate()"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="derivative.album.id" :items="relatedAlbums" :loading="relatedAlbumsLoading"
							label-key="field.Derivative.album" refreshable clearable
							:rules="rules.albumOrSeries" @input="$refs.form.validate()" @refresh="loadAlbums"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="derivative.artist.id" :items="allAuthors" :loading="allAuthorsLoading"
							label-key="field.Derivative.artist" refreshable @refresh="refreshAuthors"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="derivative.source.id" :items="allSources" label-key="field.Derivative.source"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Derivative.format')">
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
							multiple clearable
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
						<CurrencyField v-model="derivative.purchasePrice" label-key="field.Derivative.purchasePrice" />
					</v-col>
					<PriceManagement v-model="derivative" model-name="Derivative" cols="12" md="6" />
				</v-row>
			</SectionCard>

			<FormActions v-if="initialized" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script>
import { TiptapVuetify } from 'tiptap-vuetify'
import Autocomplete from '@/components/Autocomplete'
import CurrencyField from '@/components/CurrencyField'
import FormActions from '@/components/FormActions'
import PriceManagement from '@/components/PriceManagement'
import SectionCard from '@/components/SectionCard'
import { tipTapExtensions } from '@/helpers/fields'
import modelEditMixin from '@/mixins/model-edit'
import authorRefreshMixin from '@/mixins/refresh-author-list'
import imgRefreshMixin from '@/mixins/refresh-image-list'
import { integer, mandatory } from '@/helpers/rules'
import derivativeService from '@/services/derivative-service'
import sourceService from '@/services/derivative-source-service'
import typeService from '@/services/derivative-type-service'
import seriesService from '@/services/series-service'

export default {
	name: 'DerivativeEdit',

	components: {
		Autocomplete,
		CurrencyField,
		FormActions,
		PriceManagement,
		SectionCard,
		TiptapVuetify
	},

	mixins: [modelEditMixin, authorRefreshMixin, imgRefreshMixin],

	data() {
		return {
			mixinConfig: {
				modelEdit: {
					titleKeys: {
						add: 'title.add.derivative',
						edit: 'title.edit.derivative'
					},
					saveRedirectViewName: 'DerivativeView'
				}
			},

			allSeries: [],
			relatedAlbums: [],
			relatedAlbumsLoading: false,
			allSources: [],
			allTypes: [],
			derivative: {
				series: {},
				album: {},
				artist: {},
				source: {},
				type: {},
				prices: []
			},
			tipTapExtensions: tipTapExtensions,

			rules: {
				type: [mandatory()],
				colours: [integer()],
				albumOrSeries: [this.oneNotNull]
			}
		}
	},

	methods: {
		oneNotNull() {
			// In addition to this rule, an @input handler forces full form validation when one of the relevant
			// input changes. This guarantees that errors on other fields are cleared when only one changes
			if (!this.derivative.series.id && !this.derivative.album.id) {
				return this.$t('validation.Derivative.albumOrSeries')
			}
			return true
		},

		async fetchData() {
			if (this.parsedId) {
				this.derivative = await derivativeService.findById(this.parsedId)
			}

			if (!this.parsedId && this.$route.query.toSeries) {
				this.derivative.series.id = parseInt(this.$route.query.toSeries, 10)
			}
			if (!this.parsedId && this.$route.query.toAlbum) {
				this.derivative.album.id = parseInt(this.$route.query.toAlbum, 10)
			}
			if (!this.parsedId && this.$route.query.toArtist) {
				this.derivative.artist.id = parseInt(this.$route.query.toArtist, 10)
			}

			// Find all reference data
			const pSeries = seriesService.findForList()
			const pSources = sourceService.findForList()
			const pTypes = typeService.findForList()

			// Load albums with the currently available data
			this.loadAlbums()

			// Assign all reference data
			this.allSeries = await pSeries
			this.allSources = await pSources
			this.allTypes = await pTypes
		},

		async loadAlbums() {
			this.relatedAlbumsLoading = true
			this.relatedAlbums = await seriesService.findAlbumsForList(this.derivative.series.id)
			if (this.derivative.album.id) {
				// If the current album ID is not in the returned list, clear it
				if (!this.relatedAlbums.find(val => val.id === this.derivative.album.id)) {
					this.derivative.album.id = undefined
				}
			}
			this.relatedAlbumsLoading = false
		},

		saveHandler() {
			return derivativeService.save(this.derivative)
		}
	}
}
</script>
