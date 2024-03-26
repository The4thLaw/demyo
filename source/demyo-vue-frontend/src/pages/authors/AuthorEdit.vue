<template>
	<v-container fluid>
		<v-form ref="form">
			<SectionCard :subtitle="$t('fieldset.Author.identity')">
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
							v-model="author.portrait.id" :items="allImages" :loading="allImagesLoading"
							label-key="field.Author.portrait" refreshable @refresh="refreshImages"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard :subtitle="$t('fieldset.Author.biography')">
				<v-row>
					<v-col cols="12" md="6">
						<label class="dem-fieldlabel">{{ $t('field.Author.biography') }}</label>
						<tiptap-vuetify
							v-model="author.biography" :extensions="tipTapExtensions"
							:card-props="{ outlined: true }"
						/>
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field v-model="author.website" :label="$t('field.Author.website')" />
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
import imgRefreshMixin from '@/mixins/refresh-image-list'
import authorService from '@/services/author-service'

export default {
	name: 'AuthorEdit',

	components: {
		FormActions,
		SectionCard
	},

	mixins: [modelEditMixin, imgRefreshMixin],

	data() {
		return {
			mixinConfig: {
				modelEdit: {
					titleKeys: {
						add: 'title.add.author',
						edit: 'title.edit.author'
					},
					saveRedirectViewName: 'AuthorView'
				}
			},

			author: { portrait: {} },
			tipTapExtensions: tipTapExtensions,

			rules: {
				name: [
					mandatory()
				]
			}
		}
	},

	methods: {
		async fetchData() {
			if (this.parsedId) {
				this.author = await authorService.findById(this.parsedId)
			}
		},

		saveHandler() {
			return authorService.save(this.author)
		}
	}
}
</script>
