<template>
	<v-container fluid>
		<v-form ref="form">
			<template v-if="isFull">
				<SectionCard :subtitle="$t('fieldset.Author.identity')" :loading="loading">
					<v-row>
						<v-col cols="12" :md="isFull ? 4 : 6">
							<v-text-field v-model="author.firstName" :label="$t('field.Author.firstName')" />
						</v-col>
						<v-col v-if="isFull" cols="12" md="4">
							<v-text-field v-model="author.nickname" :label="$t('field.Author.nickname')" />
						</v-col>
						<v-col cols="12" :md="isFull ? 4 : 6">
							<v-text-field
								v-model="author.name" :label="$t('field.Author.name')" :rules="rules.name" required
							/>
						</v-col>
						<template v-if="isFull">
							<v-col cols="12" md="4">
								<v-text-field
									v-model="author.birthDate" :label="$t('field.Author.birthDate')" type="date"
								/>
							</v-col>
							<v-col cols="12" md="4">
								<v-text-field
									v-model="author.deathDate" :label="$t('field.Author.deathDate')" type="date"
								/>
							</v-col>
							<v-col cols="12" md="6">
								<Autocomplete
									v-model="author.portrait.id" :items="images" :loading="imagesLoading"
									label-key="field.Author.portrait" refreshable @refresh="loadImages"
								/>
							</v-col>
						</template>
					</v-row>
				</SectionCard>

				<SectionCard v-if="isFull" :subtitle="$t('fieldset.Author.biography')" :loading="loading">
					<v-row>
						<v-col cols="12" md="6">
							<span class="dem-fieldlabel">{{ $t('field.Author.biography') }}</span>
							<RichTextEditor v-model="author.biography" />
						</v-col>
						<v-col cols="12" md="6">
							<v-text-field v-model="author.website" :label="$t('field.Author.website')" />
						</v-col>
					</v-row>
				</SectionCard>

				<FormActions v-if="!loading" @save="save" @reset="reset" />
			</template>
			<v-row v-else>
				<v-col cols="12">
					<v-text-field v-model="author.firstName" :label="$t('field.Author.firstName')" />
				</v-col>
				<v-col cols="12">
					<v-text-field
						v-model="author.name" :label="$t('field.Author.name')" :rules="rules.name" required
					/>
				</v-col>
			</v-row>
		</v-form>
	</v-container>
</template>

<script setup lang="ts">
import type { EditData } from '@/composables/model-edit'
import { useLightEdit, useSimpleEdit } from '@/composables/model-edit'
import { useRefreshableImages } from '@/composables/refreshable-models'
import { mandatory } from '@/helpers/rules'
import authorService from '@/services/author-service'

const props = defineProps<{
	mode: EditMode
	saving: boolean
}>()
const emit = defineEmits<{
	save: [id: number]
	'save-error': []
}>()

const { images, imagesLoading, loadImages } = useRefreshableImages()

async function fetchData(id: number | undefined): Promise<Partial<Author>> {
	if (id) {
		return authorService.findById(id)
	}
	return Promise.resolve({
		portrait: {} as Image
	})
}

const isFull = computed(() => props.mode === 'full')

let editData : EditData<Author>
if (props.mode === 'full') {
	editData = useSimpleEdit(
		fetchData, authorService, [loadImages], 'title.add.author', 'title.edit.author', 'AuthorView')
} else {
	editData = useLightEdit(fetchData, authorService, [])
}
const { model: author, loading, save, reset } = editData

watch(() => props.saving, async (newValue) => {
	console.log('Saving switched to ', newValue)
	if (newValue) {
		console.log('Going to save')
		const id = await save()
		console.log('Saved to', id)
		if (id > 0) {
			console.log('AuthorEdit: emitting save', id)
			emit('save', id)
		} else {
			console.log('AuthorEdit: Emitting save-error')
			emit('save-error')
		}
	}
})

const rules = {
	name: [
		mandatory()
	]
}
</script>
