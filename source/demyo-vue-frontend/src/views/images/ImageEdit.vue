<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard>
				<v-row>
					<v-col :sm="12">
						<v-text-field
							v-model="image.description" :label="$t('field.Image.description')"
							:rules="rules.description"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<FormActions v-if="initialized" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script>
import FormActions from '@/components/FormActions'
import SectionCard from '@/components/SectionCard'
import { mandatory } from '@/helpers/rules'
import imageService from '@/services/image-service'

export default {
	name: 'ImageEdit',

	components: {
		FormActions,
		SectionCard
	},

	metaInfo() {
		return {
			title: this.$t('title.edit.image')
		}
	},

	data() {
		return {
			initialized: false,
			image: {},

			rules: {
				description: [
					mandatory(this)
				]
			}
		}
	},

	watch: {
		'$route': 'fetchData'
	},

	created() {
		this.$store.dispatch('ui/disableSearch')
		this.fetchData()
	},

	methods: {
		async fetchData() {
			this.$store.dispatch('ui/enableGlobalOverlay')
			const id = parseInt(this.$route.params.id, 10)
			this.image = await imageService.findById(id)
			this.$store.dispatch('ui/disableGlobalOverlay')
			this.initialized = true
		},

		async save() {
			if (!this.$refs.form.validate()) {
				return
			}
			let id = await imageService.save(this.image)
			if (id <= 0) {
				this.$store.dispatch('ui/showSnackbar', this.$t('core.exception.api.title'))
			} else {
				this.$router.push({ path: `/images/${id}/view` })
			}
		},

		reset() {
			this.$refs.form.reset()
			this.fetchData()
		}
	}
}
</script>
