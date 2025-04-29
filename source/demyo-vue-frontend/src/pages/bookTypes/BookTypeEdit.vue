<template>
	<v-container id="v-BookTypeEdit">
		<v-form ref="form">
			<SectionCard :loading="loading">
				<v-row>
					<v-col cols="12">
						<v-text-field
							v-model="bookType.name" :label="$t('field.BookType.name')" :rules="rules.name" required
						/>
					</v-col>
					<v-col cols="12">
						<span class="dem-fieldlabel">{{ $t('field.BookType.description') }}</span>
						<RichTextEditor v-model="bookType.description" />
					</v-col>
					<v-col cols="12">
						<span class="dem-fieldlabel">{{ $t('field.BookType.labelType') }}</span>
						<v-radio-group v-model="bookType.labelType" :rules="rules.labelType">
							<v-radio
								:label="$t('field.BookType.labelType.value.GRAPHIC_NOVEL')" value="GRAPHIC_NOVEL"
							/>
							<v-radio :label="$t('field.BookType.labelType.value.NOVEL')" value="NOVEL" />
						</v-radio-group>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12">
						<span class="dem-fieldlabel">{{ $t('field.BookType.fieldConfig') }}</span>
						<v-checkbox
							v-model="bookType.structuredFieldConfig"
							:label="$t('field.Album.colorists')"
							value="ALBUM_COLORIST"
						/>
						<v-checkbox
							v-model="bookType.structuredFieldConfig"
							:label="$t('field.Album.inkers')"
							value="ALBUM_INKER"
						/>
						<v-checkbox
							v-model="bookType.structuredFieldConfig"
							:label="$t('field.Album.translators')"
							value="ALBUM_TRANSLATOR"
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
import bookTypeService from '@/services/book-type-service'

async function fetchData(id :number | undefined): Promise<Partial<BookType>> {
	if (id) {
		return bookTypeService.findById(id)
	}
	return Promise.resolve({
		structuredFieldConfig: [] as ModelField[]
	})
}

const { model: bookType, loading, save, reset } = useSimpleEdit(fetchData, bookTypeService, [],
	'title.add.bookType', 'title.edit.bookType', 'BookTypeView')

const rules = {
	name: [
		mandatory()
	],
	labelType: [
		mandatory()
	]
}
</script>

<style lang="scss">
#v-BookTypeEdit {
	.v-checkbox .v-input__details {
		// This takes too much space and we don't use it here
		display: none;
	}
}
</style>
