<template>
	<v-container>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Collection.identity')">
				<v-row>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="collection.name" :label="$t('field.Collection.name')" :rules="rules.name" required
						/>
					</v-col>
					<v-col cols="12" md="4">
						<Autocomplete
							v-model="collection.publisher.id" :items="allPublishers" :loading="allPublishersLoading"
							:rules="rules.publisher" label-key="field.Collection.publisher"
							refreshable @refresh="refreshPublishers"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<Autocomplete
							v-model="collection.logo.id" :items="allImages" :loading="allImagesLoading"
							label-key="field.Collection.logo" refreshable @refresh="refreshImages"
						/>
					</v-col>
				</v-row>
				<label class="dem-fieldlabel">{{ $t('field.Collection.history') }}</label>
				<tiptap-vuetify
					v-model="collection.history" :extensions="tipTapExtensions"
					:card-props="{ outlined: true }"
				/>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Collection.internet')">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="collection.website" :label="$t('field.Collection.website')"
							:rules="rules.website" required
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="collection.feed" :label="$t('field.Collection.feed')" :rules="rules.feed" required
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
import publisherRefreshMixin from '@/mixins/refresh-publisher-list'
import collectionService from '@/services/collection-service'
import { TiptapVuetify } from 'tiptap-vuetify'

export default {
	name: 'CollectionEdit',

	components: {
		Autocomplete,
		FormActions,
		SectionCard,
		TiptapVuetify
	},

	mixins: [imgRefreshMixin, modelEditMixin, publisherRefreshMixin],

	data() {
		return {
			mixinConfig: {
				modelEdit: {
					titleKeys: {
						add: 'title.add.collection',
						edit: 'title.edit.collection'
					},
					saveRedirectViewName: 'CollectionView'
				}
			},

			collection: {
				logo: {},
				publisher: {}
			},
			tipTapExtensions: tipTapExtensions,

			rules: {
				name: [
					mandatory()
				],
				publisher: [
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
				this.collection = await collectionService.findById(this.parsedId)
			}

			if (!this.parsedId && this.$route.query.toPublisher) {
				this.collection.publisher.id = parseInt(this.$route.query.toPublisher, 10)
			}
		},

		saveHandler() {
			return collectionService.save(this.collection)
		}
	}
}
</script>
