<template>
	<component :is="layout">
		<router-view />
	</component>
</template>

<script>
import { contextRoot } from '@/myenv'
import DefaultLayout from '@/layouts/DefaultLayout'

export default {
	name: 'DemyoApp',

	metaInfo() {
		const self = this
		return {
			title: this.$t('title.home'),
			titleTemplate: '%s — Demyo',
			link: [
				{
					rel: 'manifest',
					href: `${contextRoot}manifest.json?lang=${self.$i18n.locale.replace(/-/, '_')}`
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
	// TODO[dark]: alternate colors
	--dem-text: rgba(0, 0, 0, 0.87);
	--dem-text-lighter: rgba(0, 0, 0, 0.54);
	--dem-primary-contrast: #fff;
	--dem-bg-contrast: #ddd;
	--dem-base-border: #ccc;
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
