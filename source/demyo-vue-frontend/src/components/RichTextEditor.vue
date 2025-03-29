<template>
	<!--
		TODO: eventually, import the skin from a package published on npm rather than copy-pasting it to the assets
	-->
	<Editor
		v-model="model"
		api-key="sample-api"
		:init="{
			plugins: 'autoresize lists link image help',
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
				+ ' | nonbreaking hr | removeformat | help',
			language: $t('tinymce.language'),
			// We don't need the status bar
			statusbar: false
		}"
	/>
</template>

<script setup lang="ts">
import Editor from '@tinymce/tinymce-vue'
import { useVModel } from '@vueuse/core'

/* We need the global tinymce */
import 'tinymce/tinymce'
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
import 'tinymce/plugins/autoresize'
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

const props = defineProps<{
  modelValue: string | undefined
}>()
const emit = defineEmits(['update:modelValue'])

const model = useVModel(props, 'modelValue', emit)
</script>
