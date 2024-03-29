<template>
	<v-container>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Publisher.identity')">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="publisher.name" :label="$t('field.Publisher.name')" :rules="rules.name" required
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="publisher.logo.id" :items="allImages" :loading="allImagesLoading"
							label-key="field.Publisher.logo" refreshable @refresh="refreshImages"
						/>
					</v-col>
				</v-row>
				<label class="dem-fieldlabel">{{ $t('field.Publisher.history') }}</label>
				<tiptap-vuetify
					v-model="publisher.history" :extensions="tipTapExtensions"
					:card-props="{ outlined: true }"
				/>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Publisher.internet')">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="publisher.website" :label="$t('field.Publisher.website')"
							:rules="rules.website" required
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="publisher.feed" :label="$t('field.Publisher.feed')" :rules="rules.feed" required
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<FormActions v-if="initialized" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script>
import Autocomplete from '@/components/Autocomplete.vue'
import FormActions from '@/components/FormActions.vue'
import SectionCard from '@/components/SectionCard.vue'
import { tipTapExtensions } from '@/helpers/fields'
import { mandatory, url } from '@/helpers/rules'
import modelEditMixin from '@/mixins/model-edit'
import imgRefreshMixin from '@/mixins/refresh-image-list'
import publisherService from '@/services/publisher-service'
import { TiptapVuetify } from 'tiptap-vuetify'

export default {
	name: 'PublisherEdit',

	components: {
		Autocomplete,
		FormActions,
		SectionCard,
		TiptapVuetify
	},

	mixins: [imgRefreshMixin, modelEditMixin],

	data() {
		return {
			mixinConfig: {
				modelEdit: {
					titleKeys: {
						add: 'title.add.publisher',
						edit: 'title.edit.publisher'
					},
					saveRedirectViewName: 'PublisherView'
				}
			},

			publisher: { logo: {} },
			tipTapExtensions: tipTapExtensions,

			rules: {
				name: [
					mandatory()
				],
				website: [
					url()
				],
				feed: [
					url()
				]
			}
		}
	},

	methods: {
		async fetchData() {
			if (this.parsedId) {
				this.publisher = await publisherService.findById(this.parsedId)
				// Clear the collections: we won't be editing those and don't want to save them
				delete this.publisher.collections
			}
		},

		saveHandler() {
			return publisherService.save(this.publisher)
		}
	}
}
</script>
