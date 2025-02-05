<template>
	<v-container>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Collection.identity')" :loading="loading">
				<v-row>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="collection.name" :label="$t('field.Collection.name')" :rules="rules.name" required
						/>
					</v-col>
					<v-col cols="12" md="4">
						<AutoComplete
							v-model="collection.publisher.id" :items="allPublishers" :loading="allPublishersLoading"
							:rules="rules.publisher" label-key="field.Collection.publisher"
							refreshable @refresh="refreshPublishers"
						/>
					</v-col>
					<v-col cols="12" md="4">
						<AutoComplete
							v-model="collection.logo.id" :items="allImages" :loading="allImagesLoading"
							label-key="field.Collection.logo" refreshable @refresh="refreshImages"
						/>
					</v-col>
				</v-row>
				<label class="dem-fieldlabel">{{ $t('field.Collection.history') }}</label>
				<RichTextEditor v-model="collection.history" />
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Collection.internet')" :loading="loading">
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

			<FormActions v-if="!loading" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleEdit } from '@/composables/model-edit'
import { getParsedRouteParam } from '@/helpers/route'
import { mandatory, url } from '@/helpers/rules'
import collectionService from '@/services/collection-service'
import { useRoute } from 'vue-router'

const route = useRoute()

// TODO: mixins: [imgRefreshMixin, modelEditMixin, publisherRefreshMixin],

async function fetchData(id: number|undefined): Promise<Partial<Collection>> {
	if (id) {
		return collectionService.findById(id)
	}

	const collection: Partial<Collection> = {
		publisher: {
			id: 0,
			identifyingName: '',
			name: '',
			collections: []
		},
		logo: {
			id: 0,
			identifyingName: '',
			url: '',
			userFileName: ''
		}
	}

	if (route.query.toPublisher && collection.publisher) {
		collection.publisher.id = getParsedRouteParam(route.query.toPublisher) ?? 0
	}

	return Promise.resolve(collection)
}

const {model: collection, loading, save, reset, loadData}
	= useSimpleEdit(fetchData, collectionService, 'title.add.collection', 'title.edit.collection', 'CollectionView')

const rules = ref({
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
})
</script>

