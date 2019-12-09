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
						TODO: portrait
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

			<!-- TODO: extract this to a component, where we will be able to put the CSS as well -->
			<div v-if="initialized" class="formActionButtons">
				<v-btn color="accent" @click="save">
					{{ $t('button.save') }}
				</v-btn>
				<v-btn text color="primary" @click="reset">
					{{ $t('button.reset') }}
				</v-btn>
			</div>
		</v-form>
	</v-container>
</template>

<script>
import { TiptapVuetify } from 'tiptap-vuetify'
import SectionCard from '@/components/SectionCard'
import { tipTapExtensions } from '@/helpers/fields'
import { mandatory } from '@/helpers/rules'
import authorService from '@/services/author-service'

export default {
	name: 'AuthorEdit',

	components: {
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
			this.initialized = true
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
