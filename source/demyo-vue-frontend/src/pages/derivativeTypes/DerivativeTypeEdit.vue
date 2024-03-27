<template>
	<v-container>
		<v-form ref="form">
			<SectionCard>
				<v-row>
					<v-col cols="12">
						<v-text-field
							v-model="type.name" :label="$t('field.DerivativeType.name')" :rules="rules.name" required
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
import typeService from '@/services/derivative-type-service'

export default {
	name: 'DerivativeTypeEdit',

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
						add: 'title.add.derivativeType',
						edit: 'title.edit.derivativeType'
					},
					saveRedirectViewName: 'DerivativeTypeView'
				}
			},

			type: {},

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
				this.type = await typeService.findById(this.parsedId)
			}
		},

		saveHandler() {
			return typeService.save(this.type)
		}
	}
}
</script>
