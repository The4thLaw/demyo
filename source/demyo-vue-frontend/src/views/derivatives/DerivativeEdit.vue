<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Derivative.origin')">
				<v-row>
					<v-col :sm="12" :md="6">
						<Autocomplete
							v-model="derivative.series.id" :items="allSeries" label-key="field.Derivative.series"
							@input="loadAlbums"
						/>
					</v-col>
					<v-col :sm="12" :md="6">
						<Autocomplete
							v-model="derivative.album.id" :items="relatedAlbums" label-key="field.Derivative.album"
						/>
					</v-col>
				</v-row>
			</SectionCard>
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
import derivativeService from '@/services/derivative-service'
import seriesService from '@/services/series-service'

export default {
	name: 'DerivativeEdit',

	components: {
		Autocomplete,
		FormActions,
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
			derivative: {
				series: {},
				album: {}
			},
			tipTapExtensions: tipTapExtensions,

			rules: {
				name: [
					v => !!v || this.$t('validation.mandatory')
				]
			}
		}
	},

	methods: {
		async fetchData() {
			if (this.parsedId) {
				this.derivative = await derivativeService.findById(this.parsedId)
			}
			if (!this.derivative.series) {
				this.derivative.series = {}
			}
			if (!this.derivative.album) {
				this.derivative.album = {}
			}

			// Find all reference data
			const pSeries = await seriesService.findForList()
			// Assign all reference data
			this.allSeries = await pSeries

			// Load albums with the currently available data
			this.loadAlbums()
		},

		async loadAlbums() {
			this.relatedAlbums = await seriesService.findAlbumsForList(this.derivative.series.id)
			// TODO: if the current album ID is not in the returned list, clear it
		},

		saveHandler() {
			return derivativeService.save(this.derivative)
		}
	}
}
</script>
