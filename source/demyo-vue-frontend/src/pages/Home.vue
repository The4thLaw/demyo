<template>
	<v-container id="v-Home">
		<SectionCard :title="$t('widget.search.title')">
			<p class="mt-8 mb-2" v-text="$t('widget.search.description')" />

			<v-text-field
				v-model="quicksearchQuery" clearable hide-details
				:placeholder="$t('widget.search.label')"
				prepend-icon="mdi-magnify" @keyup="performSearch"
				@click:clear="clearSearch"
			/>

			---
			<!--
			TODO: import skin from package published on npm
			TODO: extract our own component with a v-model to abstract the TinyMCE code (and share a common config). See https://stackoverflow.com/a/72488822/109813
			-->
			<Editor
				api-key="sample-api"
				:init="{
					plugins: 'lists link image table code help wordcount',
					license_key: 'gpl',
					// We sideload our skin, no need for TinyMCE to manage it
					skin: false,
					content_css: 'default',
					// No branding or promotion, we'll have that in our About page
					promotion: false,
					branding: false,
					// Customize the toolbar
					menubar: false,
					toolbar: 'undo redo | styles | bold italic underline | link unlink image'
						+ ' | alignleft aligncenter alignright alignjustify alignnone '
						+ ' blockquote bullist numlist indent outdent'
						+ ' | hr | removeformat | help',
					language: $t('tinymce.language'),
					// We don't need the status bar
					statusbar: false
				}"
			/>
			---
		</SectionCard>

		<QuickSearchResults
			v-if="isRelevantSearchQuery" :results="quicksearchResults" :loading="quicksearchLoading"
		/>
	</v-container>
</template>

<script>
import quicksearch from '@/mixins/quicksearch'
import { useUiStore } from '@/stores/ui'
import Editor from '@tinymce/tinymce-vue'
/* Default icons are required. After that, import custom icons if applicable */
import 'tinymce/icons/default/icons.min.js'

/* Required TinyMCE components */
import 'tinymce/models/dom/model.min.js'
import 'tinymce/themes/silver/theme.min.js'

/* Import a skin (can be a custom skin instead of the default) */
// import 'tinymce/skins/ui/oxide/skin.js'
import '@/assets/deeper-skin.css'
import '@/assets/deeper-vuetify-bindings.css'

/* Import plugins */
import 'tinymce/plugins/help'
import 'tinymce/plugins/help/js/i18n/keynav/en'
import 'tinymce/plugins/help/js/i18n/keynav/fr_FR'
import 'tinymce/plugins/image'
import 'tinymce/plugins/link'
import 'tinymce/plugins/lists'
// import 'tinymce/plugins/table'

/* Import languages. Eventually, lazy-load them? */
import 'tinymce-i18n/langs7/fr_FR'

/* The default content CSS can be changed or replaced with appropriate CSS for the editor content. */
import 'tinymce/skins/content/default/content.js'

export default {
	name: 'HomePage',

	components: {
		Editor
	},

	mixins: [quicksearch],

	data() {
		return {
			uiStore: useUiStore()
		}
	},

	head() {
		return {
			title: this.$t('title.home')
		}
	},

	created() {
		this.uiStore.disableSearch()
		this.uiStore.disableGlobalOverlay()
	}
}
</script>
