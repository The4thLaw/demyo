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
						<AutoComplete
							v-model="publisher.logo.id" :items="allImages" :loading="allImagesLoading"
							label-key="field.Publisher.logo" refreshable @refresh="refreshImages"
						/>
					</v-col>
				</v-row>
				<label class="dem-fieldlabel">{{ $t('field.Publisher.history') }}</label>
				<RichTextEditor v-model="publisher.history" />
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
import FormActions from '@/components/FormActions.vue'
import SectionCard from '@/components/SectionCard.vue'
import { mandatory, url } from '@/helpers/rules'
import modelEditMixin from '@/mixins/model-edit'
import imgRefreshMixin from '@/mixins/refresh-image-list'
import publisherService from '@/services/publisher-service'

export default {
	name: 'PublisherEdit',

	components: {
		FormActions,
		SectionCard
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

	head() {
		return { title: this.pageTitle }
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
