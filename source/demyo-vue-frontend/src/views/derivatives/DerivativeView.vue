<template>
	<v-container>
		<DnDImage other-images-label="field.Derivative.images" @save="saveDndImages" />
		Foo
	</v-container>
</template>

<script>
import DnDImage from '@/components/DnDImage'
import derivativeService from '@/services/derivative-service'

export default {
	name: 'DerivativeView',

	components: {
		DnDImage
	},

	watch: {
		'$route': 'fetchData'
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			//this.mainLoading = true
			//const id = parseInt(this.$route.params.id, 10)

			/*
			this.derivative = await derivativeService.findById(id)
			this.mainLoading = false
			*/
		},

		async saveDndImages(data) {
			// TODO: use derivative.id instead of parsing
			const id = parseInt(this.$route.params.id, 10)
			let ok = await derivativeService.saveFilepondImages(id, data.otherImages)
			if (ok) {
				this.$store.dispatch('ui/showSnackbar', this.$t('draganddrop.snack.confirm'))
				// TODO: refresh the data to show the images
			} else {
				this.$store.dispatch('ui/showSnackbar', this.$t('core.exception.api.title'))
			}
		}
	}
}
</script>
