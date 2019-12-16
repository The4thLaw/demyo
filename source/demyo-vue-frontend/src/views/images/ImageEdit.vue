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
import { saveStub } from '@/helpers/actions'
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

		save() {
			saveStub(this, () => {
				return imageService.save(this.image)
			}, 'ImageView')
		},

		reset() {
			this.$refs.form.reset()
			this.fetchData()
		}
	}
}
</script>
