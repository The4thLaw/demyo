<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard>
				<v-row>
					<v-col cols="12">
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
import { mandatory } from '@/helpers/rules'
import modelEditMixin from '@/mixins/model-edit'
import imageService from '@/services/image-service'

export default {
	name: 'ImageEdit',

	mixins: [modelEditMixin],

	data() {
		return {
			mixinConfig: {
				modelEdit: {
					titleKeys: {
						add: '-',
						edit: 'title.edit.image'
					},
					saveRedirectViewName: 'ImageView'
				}
			},

			image: {},

			rules: {
				description: [
					mandatory()
				]
			}
		}
	},

	head() {
		return { title: this.pageTitle }
	},

	methods: {
		async fetchData() {
			this.image = await imageService.findById(this.parsedId)
		},

		saveHandler() {
			return imageService.save(this.image)
		}
	}
}
</script>
