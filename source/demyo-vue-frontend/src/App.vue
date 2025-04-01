<template>
	<component :is="layout">
		<router-view />
	</component>
</template>

<script setup lang="ts">
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import { contextRoot } from '@/myenv'
import { useHead } from '@unhead/vue'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'

const i18n = useI18n()
const route = useRoute()

useHead({
	titleTemplate: (title) => !title ? 'Demyo' : `${title} – Demyo`,
	link: () => [
		{
			rel: 'manifest',
			href: `${contextRoot}manifest.json?lang=${i18n.locale.value.replace(/-/, '_')}`
		}
	]
})

// Inspired by:
// https://itnext.io/b379baa91a05
// https://markus.oberlehner.net/blog/dynamic-vue-layout-components/
const layout = computed(() => route.meta.layout || DefaultLayout)

document.body.addEventListener('click', (e: MouseEvent) => {
	if (!(e.target instanceof HTMLElement)) {
		return
	}
	// Force external links to leave Android/iOS app mode, and open them in new tabs for regular browsers
	if (e.target.matches('a[href^="http://"], a[href^="https://"], a[href^="ftp://"]')) {
		e.target.setAttribute('target', '_blank')
		e.target.setAttribute('rel', 'noopener, noreferer, external')
	}
})
</script>

<style lang="scss">
@use '@/styles/main';
</style>
