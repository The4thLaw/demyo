<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Tag.identity')" :loading="loading">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="tag.name" :label="$t('field.Tag.name')" :rules="rules.name" required
						/>
					</v-col>
					<v-col cols="12" md="6">
						<span class="dem-fieldlabel">
							{{ $t('field.Tag.preview') }}
						</span>
						<div>
							<span :style="style" class="d-Tag">
								{{ tag.name }}
							</span>
						</div>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="6">
						<span class="dem-fieldlabel">{{ $t('field.Tag.description') }}</span>
						<RichTextEditor v-model="tag.description" />
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Tag.colours')">
				<v-row>
					<v-col cols="12" md="6">
						<span class="dem-fieldlabel">
							{{ $t('field.Tag.fgColour') }}
						</span>
						<v-checkbox v-model="noFgColour" :label="$t('special.form.noColour')" />
						<v-color-picker v-if="!noFgColour" v-model="tag.fgColour" />
					</v-col>
					<v-col cols="12" md="6">
						<span class="dem-fieldlabel">
							{{ $t('field.Tag.bgColour') }}
						</span>
						<v-checkbox v-model="noBgColour" :label="$t('special.form.noColour')" />
						<v-color-picker v-if="!noBgColour" v-model="tag.bgColour" />
					</v-col>
				</v-row>
			</SectionCard>

			<FormActions v-if="!loading" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script setup lang="ts">
import { useSimpleEdit } from '@/composables/model-edit'
import { useTagStyle } from '@/composables/tags'
import { mandatory } from '@/helpers/rules'
import tagService from '@/services/tag-service'

const noFgColour = ref(true)
const noBgColour = ref(true)

async function fetchData(id: number | undefined): Promise<Partial<Tag>> {
	if (!id) {
		const skeleton: Partial<Tag> = {}
		return Promise.resolve(skeleton)
	}

	return tagService.editById(id)
		.then(t => {
			noFgColour.value = !t.fgColour
			noBgColour.value = !t.bgColour
			return t
		})
}

const { model: tag, loading, save, reset } = useSimpleEdit(fetchData, tagService,
	[], 'title.add.tag', 'title.edit.tag', 'TagView')

const style = useTagStyle(tag)

watch(noBgColour, (newFlag) => {
	if (newFlag) {
		tag.value.bgColour = undefined
	}
})
watch(noFgColour, (newFlag) => {
	if (newFlag) {
		tag.value.fgColour = undefined
	}
})

const rules = {
	name: [
		mandatory()
	]
}
</script>
