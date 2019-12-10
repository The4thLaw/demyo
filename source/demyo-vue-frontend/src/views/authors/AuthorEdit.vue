<template>
	<v-container fluid>
		<v-form ref="form">
			TODO: handle add / edit
			<SectionCard>
				<h2 class="subtitle-1 primary--text">
					{{ $t('fieldset.Author.identity') }}
				</h2>
				<v-row>
					<v-col :sm="12" :md="4">
						<v-text-field v-model="author.firstName" :label="$t('field.Author.firstName')" />
					</v-col>
					<v-col :sm="12" :md="4">
						<v-text-field v-model="author.nickname" :label="$t('field.Author.nickname')" />
					</v-col>
					<v-col :sm="12" :md="4">
						<v-text-field v-model="author.name" :label="$t('field.Author.name')" :rules="rules.name" required />
					</v-col>
					<v-col :sm="12" :md="6">
						<v-autocomplete
							v-model="author.portrait.id"
							:items="allImages"
							:label="$t('field.Author.portrait')"
							item-text="identifyingName"
							item-value="id"
							menu-props="allowOverflow"
						/>
					</v-col>
				</v-row>
			</SectionCard>

			<SectionCard>
				<h2 class="subtitle-1 primary--text">
					{{ $t('fieldset.Author.biography') }}
				</h2>
				<v-row>
					<v-col :sm="12" :md="6">
						<label class="fieldLabel">{{ $t('field.Author.biography') }}</label>
						<tiptap-vuetify
							v-model="author.biography"
							:extensions="tipTapExtensions"
							:card-props="{ outlined: true }"
						/>
					</v-col>
					<v-col :sm="12" :md="6">
						<v-text-field v-model="author.website" :label="$t('field.Author.website')" />
					</v-col>
				</v-row>
			</SectionCard>

			<FormActions v-if="initialized" />
		</v-form>
	</v-container>
</template>

<script>
import { TiptapVuetify } from 'tiptap-vuetify'
import FormActions from '@/components/FormActions'
import SectionCard from '@/components/SectionCard'
import { tipTapExtensions } from '@/helpers/fields'
import { mandatory } from '@/helpers/rules'
import authorService from '@/services/author-service'
import imageService from '@/services/image-service'

export default {
	name: 'AuthorEdit',

	components: {
		FormActions,
		SectionCard,
		TiptapVuetify
	},

	metaInfo() {
		return {
			title: this.initialized
				? (this.author.id ? this.$t('title.edit.author') : this.$t('title.add.author'))
				: ''
		}
	},

	data() {
		return {
			initialized: false,
			allImages: [],
			author: {},
			tipTapExtensions: tipTapExtensions,

			rules: {
				name: [
					mandatory(this)
				]
			}
		}
	},

	watch: {
		'$route': 'fetchData'
	},

	created() {
		this.fetchData()
	},

	methods: {
		async fetchData() {
			if (this.$route.params.id) { // Edit mode -> load the author
				this.$store.dispatch('ui/enableGlobalOverlay')
				const id = parseInt(this.$route.params.id, 10)
				this.author = await authorService.findById(id)
				this.$store.dispatch('ui/disableGlobalOverlay')
			}
			if (!this.author.portrait) {
				this.author.portrait = {
					id: undefined
				}
			}
			this.allImages = await imageService.findForIndex()
			this.initialized = true
		},

		save() {
			if (!this.$refs.form.validate()) {
				return
			}
			// TODO: implement save
		},

		reset() {
			this.$refs.form.reset()
			if (this.author.id) {
				this.fetchData()
			}
		}
	}
}
</script>
