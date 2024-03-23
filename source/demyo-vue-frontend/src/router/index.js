import { contextRoot } from '@/myenv'
import Home from '@/pages/Home.vue'
import { useUiStore } from '@/stores/ui'
import { createRouter, createWebHistory } from 'vue-router'
import albumRoutes from './albums'
import authorRoutes from './authors'
import bindingRoutes from './bindings'
import collectionRoutes from './collections'
import derivativeSourceRoutes from './derivative-sources'
import derivativeTypeRoutes from './derivative-types'
import derivativeRoutes from './derivatives'
import imageRoutes from './images'
import managementRoutes from './manage'
import publisherRoutes from './publishers'
import readerRoutes from './readers'
import seriesRoutes from './series'
import tagRoutes from './tags'

// TODO: Vue 3: restore equivalent to vue-meta

const routes = [
	{
		path: '/',
		name: 'home',
		component: Home
	},
	...albumRoutes,
	...authorRoutes,
	...bindingRoutes,
	...collectionRoutes,
	...derivativeRoutes,
	...derivativeSourceRoutes,
	...derivativeTypeRoutes,
	...imageRoutes,
	...managementRoutes,
	...publisherRoutes,
	...readerRoutes,
	...seriesRoutes,
	...tagRoutes,
	{
		path: '/about',
		name: 'about',
		component: () => import('@/pages/About.vue')
	}
]

const processBase = import.meta.env.BASE_URL || ''
let baseUrl = `/${processBase}${contextRoot}`
// Ensure this does not lead to duplicate slashes, which (1) is not correct and (2) confuses the router
baseUrl = baseUrl.replaceAll(/\/\/+/g, '/')
console.log('Initializing Vue router with base:', baseUrl)
const router = createRouter({
	history: createWebHistory(),
	base: baseUrl,
	routes,
	scrollBehavior(_to, _from, savedPosition) {
		// May not work due to https://github.com/vuejs/vue-router/issues/1187
		if (savedPosition) {
			return savedPosition
		}
		return { left: 0, top: 0 }
	}
})

// Reset the UI on page transitions:
// - Enable the search bar
router.beforeEach((to, from, next) => {
	const uiStore = useUiStore()
	uiStore.enableSearch()
	next()
})

export default router
