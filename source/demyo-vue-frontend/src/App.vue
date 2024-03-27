<template>
	<component :is="layout">
		<router-view />
	</component>
</template>

<script>
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import { contextRoot } from '@/myenv'

export default {
	name: 'DemyoApp',

	head() {
		return {
			titleTemplate: (title) => !title ? 'Demyo' : `${title} – Demyo`,
			link: [
				{
					rel: 'manifest',
					href: `${contextRoot}manifest.json?lang=${this.$i18n.locale.replace(/-/, '_')}`
				}
			]
		}
	},

	computed: {
		layout() {
			// Inspired by:
			// https://itnext.io/b379baa91a05
			// https://markus.oberlehner.net/blog/dynamic-vue-layout-components/
			return this.$route.meta.layout || DefaultLayout
		}
	},

	created() {
		// Force external links to leave Android/iOS app mode, and open them in new tabs for regular browsers
		document.body.addEventListener('click', e => {
			if (e.target.matches('a[href^="http://"], a[href^="https://"], a[href^="ftp://"]')) {
				e.target.setAttribute('target', '_blank')
				e.target.setAttribute('rel', 'noopener, noreferer, external')
			}
		})
	}
}
</script>

<style lang="less">
:root {
	/* stylelint-disable-next-line color-function-notation */
	--dem-text: rgba(var(--v-theme-on-surface), var(--v-high-emphasis-opacity));
	/* stylelint-disable-next-line color-function-notation */
	--dem-text-lighter: rgba(var(--v-theme-on-surface), var(--v-medium-emphasis-opacity));
}

@media print {

	// Solution from https://stackoverflow.com/a/23778125/109813
	@page {
		margin: 0;
	}

	html {
		background-color: #fff;
		margin: 0;
	}

	body {
		margin: 10mm 15mm;
	}

	.no-print {
		display: none !important;
	}
}
</style>
