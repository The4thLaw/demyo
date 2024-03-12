<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Tag.identity')">
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field
							v-model="tag.name" :label="$t('field.Tag.name')" :rules="rules.name" required
						/>
					</v-col>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">
							{{ $t('field.Tag.preview') }}
						</label>
						<div>
							<span :style="style" class="d-Tag">
								{{ tag.name }}
							</span>
						</div>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">{{ $t('field.Tag.description') }}</label>
						<tiptap-vuetify
							v-model="tag.description" :extensions="tipTapExtensions"
							:card-props="{ outlined: true }"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Tag.colours')">
				<v-row>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">
							{{ $t('field.Tag.fgColour') }}
						</label>
						<v-checkbox v-model="noFgColour" :label="$t('special.form.noColour')" />
						<v-color-picker v-if="!noFgColour" v-model="tag.fgColour" flat @input="updateFg" />
					</v-col>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">
							{{ $t('field.Tag.bgColour') }}
						</label>
						<v-checkbox v-model="noBgColour" :label="$t('special.form.noColour')" />
						<v-color-picker v-if="!noBgColour" v-model="tag.bgColour" flat @input="updateBg" />
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
import { tipTapExtensions } from '@/helpers/fields'
import { mandatory } from '@/helpers/rules'
import modelEditMixin from '@/mixins/model-edit'
import tagService from '@/services/tag-service'
import { TiptapVuetify } from 'tiptap-vuetify'

export default {
	name: 'TagEdit',

	components: {
		FormActions,
		SectionCard,
		TiptapVuetify
	},

	mixins: [modelEditMixin],

	data() {
		return {
			mixinConfig: {
				modelEdit: {
					titleKeys: {
						add: 'title.add.tag',
						edit: 'title.edit.tag'
					},
					saveRedirectViewName: 'TagView'
				}
			},

			tag: {},
			noFgColour: true,
			noBgColour: true,
			// For some reason, reactivity is not properly applied on tag.fgColour
			// The propery exists and is accessed when the computed method is called the first time so
			// no idea why it doesn't work. It could be an issue with the way v-color-picker deals
			// with the value
			fgColour: '',
			bgColour: '',

			tipTapExtensions: tipTapExtensions,

			rules: {
				name: [
					mandatory()
				]
			}
		}
	},

	computed: {
		style() {
			const style = {}
			if (!this.noFgColour) {
				style.color = this.fgColour
			}
			if (!this.noBgColour) {
				style['background-color'] = this.bgColour
			}
			return style
		}
	},

	methods: {
		async fetchData() {
			if (this.parsedId) {
				this.tag = await tagService.findById(this.parsedId)
			}

			this.noFgColour = !this.tag.fgColour
			if (this.noFgColour) {
				// Needed for Vuetify to provide colors as a hex string
				this.tag.fgColour = ''
			}
			// Needed for the reactivity issues
			this.fgColour = this.tag.fgColour || ''
			this.noBgColour = !this.tag.bgColour
			if (this.noBgColour) {
				this.tag.bgColour = ''
			}
			this.bgColour = this.tag.bgColour || ''
			console.log('loaded')
		},

		updateFg(c) {
			this.fgColour = c
		},

		updateBg(c) {
			this.bgColour = c
		},

		saveHandler() {
			// Truncate RGBA color
			if (this.noFgColour) {
				this.tag.fgColour = null
			} else if (this.tag.fgColour) {
				this.tag.fgColour = this.tag.fgColour.substring(0, 7)
			}
			if (this.noBgColour) {
				this.tag.bgColour = null
			} else if (this.tag.bgColour) {
				this.tag.bgColour = this.tag.bgColour.substring(0, 7)
			}

			return tagService.save(this.tag)
		}
	}
}
</script>
