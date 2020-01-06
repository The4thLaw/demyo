<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Derivative.origin')">
				<v-row>
					<v-col :sm="12" :md="6">
						<Autocomplete
							v-model="derivative.series.id" :items="allSeries" label-key="field.Derivative.series"
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
	name: 'AuthorEdit',

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

	created() { // TODO: Maybe this disableSearch, watch and auto-reload should be mixins ?
		this.$store.dispatch('ui/disableSearch')
		this.fetchData()
	},

	methods: {
		async fetchData() {
			// TODO: stub this
			if (this.$route.params.id) { // Edit mode -> load
				this.$store.dispatch('ui/enableGlobalOverlay')
				const id = parseInt(this.$route.params.id, 10)
				this.derivative = await derivativeService.findById(id)
				this.$store.dispatch('ui/disableGlobalOverlay')
			}
			if (!this.derivative.series) {
				this.derivative.series = {
					id: undefined
				}
			}
			this.allImages = await imageService.findForList()
			this.allSeries = await seriesService.findForList()
			this.initialized = true
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
