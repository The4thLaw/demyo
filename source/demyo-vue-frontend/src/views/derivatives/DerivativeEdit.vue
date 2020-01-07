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
import { saveStub } from '@/helpers/actions'
import { tipTapExtensions } from '@/helpers/fields'
import authorService from '@/services/author-service'
import derivativeService from '@/services/derivative-service'
import imageService from '@/services/image-service'
import seriesService from '@/services/series-service'

export default {
	name: 'DerivativeEdit',

	components: {
		Autocomplete,
		FormActions,
		SectionCard,
		TiptapVuetify
	},

	metaInfo() {
		return {
			title: this.initialized
				? (this.derivative.id ? this.$t('title.edit.derivative') : this.$t('title.add.derivative'))
				: ''
		}
	},

	data() {
		return {
			initialized: false,
			allSeries: [],
			relatedAlbums: [],
			allImages: [],
			allImagesLoading: false,
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

	watch: {
		'$route': 'fetchData'
	},

	created() {
		// TODO: Maybe this disableSearch, watch and auto-reload should be mixins ?
		// The mixin could call the fetchData and handle the overlay
		// Perhaps we could also have mixins for reloadable images, etc
		this.$store.dispatch('ui/disableSearch')
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.$store.dispatch('ui/enableGlobalOverlay')
			// TODO: stub this? Maybe it should be a mixin?
			if (this.$route.params.id) { // Edit mode -> load
				const id = parseInt(this.$route.params.id, 10)
				this.derivative = await derivativeService.findById(id)
			}
			if (!this.derivative.series) {
				this.derivative.series = {}
			}
			if (!this.derivative.album) {
				this.derivative.album = {}
			}

			// Find all reference data
			const pImages = await imageService.findForList()
			const pSeries = await seriesService.findForList()
			const pAuthors = await authorService.findForList()
			// Assign all reference data
			this.allImages = await pImages
			this.allSeries = await pSeries
			this.allAuthors = await pAuthors

			// Load albums with the currently available data
			this.loadAlbums()

			this.$store.dispatch('ui/disableGlobalOverlay')
			this.initialized = true
		},

		async loadAlbums() {
			this.relatedAlbums = await seriesService.findAlbumsForList(this.derivative.series.id)
			// TODO: if the current album ID is not in the returned list, clear it
		},

		async refreshImages() {
			this.allImagesLoading = true
			this.allImages = await imageService.findForList()
			this.allImagesLoading = false
		},

		save() {
			saveStub(this, () => {
				return derivativeService.save(this.derivative)
			}, 'DerivativeView')
		},

		reset() {
			this.$refs.form.reset()
			if (this.derivative.id) {
				this.fetchData()
			}
		}
	}
}
</script>
