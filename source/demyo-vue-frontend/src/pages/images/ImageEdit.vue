<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :loading="loading">
				<v-row>
					<v-col cols="12">
						<v-text-field
							v-model="image.description" :label="$t('field.Image.description')"
							:rules="rules.description"
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
import { mandatory } from '@/helpers/rules'
import imageService from '@/services/image-service'

async function fetchData(id: number | undefined): Promise<Image> {
	if (!id) {
		throw new Error("Can't add an image this way")
	}
	return imageService.editById(id)
}

const { model: image, loading, save, reset } = useSimpleEdit(
	fetchData, imageService, [], '-', 'title.edit.image', 'ImageView')

const rules = {
	description: [mandatory()]
}
</script>
