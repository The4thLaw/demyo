<template>
	<component :is="layout">
		<router-view />
	</component>
</template>

<script>
import DefaultLayout from '@/layouts/DefaultLayout'

export default {
	name: 'DemyoApp',

	metaInfo() {
		return {
			title: this.$t('title.home'),
			titleTemplate: '%s — Demyo'
		}
	},

	computed: {
		layout() {
			// Inspired by:
			// https://itnext.io/b379baa91a05
			// https://markus.oberlehner.net/blog/dynamic-vue-layout-components/
			return this.$route.meta.layout || DefaultLayout
		}
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
