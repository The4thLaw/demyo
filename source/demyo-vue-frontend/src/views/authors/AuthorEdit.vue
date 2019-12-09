<template>
	<v-container fluid>
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
					<v-text-field v-model="author.name" :label="$t('field.Author.name')" />
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

		TODO: buttons to save/cancel
	</v-container>
</template>

<script>
import { TiptapVuetify } from 'tiptap-vuetify'
import SectionCard from '@/components/SectionCard'
import { tipTapExtensions } from '@/helpers/fields'
import authorService from '@/services/author-service'

export default {
	name: 'AuthorEdit',

	components: {
		SectionCard,
		TiptapVuetify
	},

	metaInfo() {
		return {
			// TODO: change title for ADD
			title: this.author.identifyingName
		}
	},

	data() {
		return {
			author: {},
			tipTapExtensions: tipTapExtensions
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
			this.$store.dispatch('ui/enableGlobalOverlay')
			const id = parseInt(this.$route.params.id, 10)
			this.author = await authorService.findById(id)
			this.$store.dispatch('ui/disableGlobalOverlay')
		}
	}
}
</script>
