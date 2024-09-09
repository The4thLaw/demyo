<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Reader.identification')">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="reader.name" :label="$t('field.Reader.name')" :rules="rules.name" required
						/>
					</v-col>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">
							{{ $t('field.Reader.colour') }}
						</label>
						<v-checkbox v-model="noColour" :label="$t('special.form.noColour')" />
						<v-color-picker v-if="!noColour" v-model="reader.colour" />
					</v-col>
				</v-row>
			</SectionCard>

			<FormActions v-if="initialized" @save="save" @reset="reset" />
		</v-form>
	</v-container>
</template>

<script>
import { mandatory } from '@/helpers/rules'
import modelEditMixin from '@/mixins/model-edit'
import readerService from '@/services/reader-service'

export default {
	name: 'ReaderEdit',

	mixins: [modelEditMixin],

	data() {
		return {
			mixinConfig: {
				modelEdit: {
					titleKeys: {
						add: 'title.add.reader',
						edit: 'title.edit.reader'
					},
					saveRedirectViewName: 'ReaderView'
				}
			},

			reader: {},
			noColour: true,

			rules: {
				name: [
					mandatory()
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
				this.reader = await readerService.findById(this.parsedId)
			}

			this.noColour = !this.reader.colour
			if (!this.reader.colour) {
				this.reader.colour = ''
			}
		},

		saveHandler() {
			// Truncate RGBA color
			if (this.noColour) {
				this.reader.colour = null
			} else if (this.reader.colour) {
				this.reader.colour = this.reader.colour.substring(0, 7)
			}
			return readerService.save(this.reader)
		}
	}
}
</script>
