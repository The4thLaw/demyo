<template>
	<v-container>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Publisher.identity')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="publisher.name" :label="$t('field.Publisher.name')" :rules="rules.name" required
						/>
					</v-col>
					<v-col cols="12" md="6">
						<Autocomplete
							v-model="publisher.logo.id" :items="images" :loading="imagesLoading"
							label-key="field.Publisher.logo" refreshable @refresh="loadImages"
						/>
					</v-col>
				</v-row>
				<span class="dem-fieldlabel">{{ $t('field.Publisher.history') }}</span>
				<RichTextEditor v-model="publisher.history" />
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Publisher.internet')" :loading="loading">
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

			<FormActions v-if="!loading" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleEdit } from '@/composables/model-edit'
import { useRefreshableImages } from '@/composables/refreshable-models'
import { mandatory, url } from '@/helpers/rules'
import publisherService from '@/services/publisher-service'

const { images, imagesLoading, loadImages } = useRefreshableImages()

async function fetchData(id: number | undefined): Promise<Partial<Publisher>> {
	if (id) {
		return publisherService.findById(id)
			// Clear the collections: we won't be editing those and don't want to save them
			.then(p => {
				p.collections = null as unknown as Collection[]
				return p
			})
	}

	const skeleton: Partial<Publisher> = {
		logo: {} as Image
	}

	return Promise.resolve(skeleton)
}

const { model: publisher, loading, save, reset } = useSimpleEdit(fetchData, publisherService,
	[loadImages], 'title.add.publisher', 'title.edit.publisher', 'PublisherView')

const rules = {
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
</script>
