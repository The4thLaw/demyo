<template>
	<v-container>
		<v-form ref="form">
			<SectionCard>
				<v-row>
					<v-col cols="12">
						<v-text-field
							v-model="binding.name" :label="$t('field.Binding.name')" :rules="rules.name" required
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<FormActions v-if="initialized" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script>
import FormActions from '@/components/FormActions.vue'
import SectionCard from '@/components/SectionCard.vue'
import { mandatory } from '@/helpers/rules'
import modelEditMixin from '@/mixins/model-edit'
import bindingService from '@/services/binding-service'

export default {
	name: 'BindingEdit',

	components: {
		FormActions,
		SectionCard
	},

	mixins: [modelEditMixin],

	data() {
		return {
			mixinConfig: {
				modelEdit: {
					titleKeys: {
						add: 'title.add.binding',
						edit: 'title.edit.binding'
					},
					saveRedirectViewName: 'BindingView'
				}
			},

			binding: {},

			rules: {
				name: [
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
			if (this.parsedId) {
				this.binding = await bindingService.findById(this.parsedId)
			}
		},

		saveHandler() {
			return bindingService.save(this.binding)
		}
	}
}
</script>
