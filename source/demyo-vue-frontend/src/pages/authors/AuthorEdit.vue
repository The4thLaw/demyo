<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Author.identity')" :loading="loading">
				<v-row>
					<v-col cols="12" md="4">
						<v-text-field v-model="author.firstName" :label="$t('field.Author.firstName')" />
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field v-model="author.nickname" :label="$t('field.Author.nickname')" />
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field
							v-model="author.name" :label="$t('field.Author.name')" :rules="rules.name" required
						/>
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field v-model="author.birthDate" :label="$t('field.Author.birthDate')" type="date" />
					</v-col>
					<v-col cols="12" md="4">
						<v-text-field v-model="author.deathDate" :label="$t('field.Author.deathDate')" type="date" />
					</v-col>
					<v-col cols="12" md="6">
						<AutoComplete
							v-model="author.portrait.id" :items="images" :loading="imagesLoading"
							label-key="field.Author.portrait" refreshable @refresh="loadImages"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Author.biography')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">{{ $t('field.Author.biography') }}</label>
						<RichTextEditor v-model="author.biography" />
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field v-model="author.website" :label="$t('field.Author.website')" />
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
import { mandatory } from '@/helpers/rules'
import authorService from '@/services/author-service'

const { images, imagesLoading, loadImages } = useRefreshableImages()

async function fetchData(id: number | undefined): Promise<Partial<Author>> {
	if (id) {
		return authorService.findById(id)
	}
	return Promise.resolve({
		portrait: {} as Image
	})
}

const { model: author, loading, save, reset } = useSimpleEdit(fetchData, authorService, [loadImages], 'title.add.author', 'title.edit.author', 'AuthorView')

const rules = {
	name: [
		mandatory()
	]
}
</script>
