<template>
	<v-container>
		<v-form ref="form">
			<SectionCard>
				<v-row>
					<v-col cols="12">
						<v-text-field
							v-model="source.name" :label="$t('field.DerivativeSource.name')"
							:rules="rules.name" required
						/>
					</v-col>
				</v-row>
			</SectionCard>
			<SectionCard :subtitle="$t('fieldset.DerivativeSource.contact')">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field v-model="source.owner" :label="$t('field.DerivativeSource.owner')" />
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="source.phoneNumber" :label="$t('field.DerivativeSource.phoneNumber')"
							:rules="rules.phoneNumber"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="source.email" :label="$t('field.DerivativeSource.email')"
							:rules="rules.email"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="source.website" :label="$t('field.DerivativeSource.website')"
							:rules="rules.website"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-textarea
							v-model="source.address" :label="$t('field.DerivativeSource.address')"
							rows="3"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('field.DerivativeSource.history')">
				<tiptap-vuetify
					v-model="source.history" :extensions="tipTapExtensions"
					:card-props="{ outlined: true }"
				/>
			</SectionCard>

			<FormActions v-if="initialized" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script>
import { TiptapVuetify } from 'tiptap-vuetify'
import FormActions from '@/components/FormActions'
import SectionCard from '@/components/SectionCard'
import { tipTapExtensions } from '@/helpers/fields'
import { email, mandatory, phone, url } from '@/helpers/rules'
import modelEditMixin from '@/mixins/model-edit'
import sourceService from '@/services/derivative-source-service'

export default {
	name: 'DerivativeSourceEdit',

	components: {
		FormActions,
		SectionCard,
		TiptapVuetify
	},

	mixins: [modelEditMixin],

	data() {
		return {
			mixinConfig: {
				modelEdit: {
					titleKeys: {
						add: 'title.add.derivativeSource',
						edit: 'title.edit.derivativeSource'
					},
					saveRedirectViewName: 'DerivativeSourceView'
				}
			},

			source: {},
			tipTapExtensions: tipTapExtensions,

			rules: {
				name: [
					mandatory(this)
				],
				email: [
					email(this)
				],
				website: [
					url(this)
				],
				phoneNumber: [
					phone(this)
				]
			}
		}
	},

	methods: {
		async fetchData() {
			if (this.parsedId) {
				this.source = await sourceService.findById(this.parsedId)
			}
		},

		saveHandler() {
			return sourceService.save(this.source)
		}
	}
}
</script>
