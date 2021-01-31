import Vue from 'vue'
import VueMeta from 'vue-meta'
import VueRouter from 'vue-router'
import store from '@/store/index'
import Home from '@/pages/Home.vue'
import albumRoutes from './albums'
import authorRoutes from './authors'
import bindingRoutes from './bindings'
import collectionRoutes from './collections'
import derivativeRoutes from './derivatives'
import derivativeSourceRoutes from './derivative-sources'
import derivativeTypeRoutes from './derivative-types'
import imageRoutes from './images'
import managementRoutes from './manage'
import publisherRoutes from './publishers'
import readerRoutes from './readers'
import seriesRoutes from './series'
import tagRoutes from './tags'

Vue.use(VueRouter)
Vue.use(VueMeta)

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
		// route level code-splitting
		// this generates a separate chunk (admin.[hash].js) for this route
		// which is lazy-loaded when the route is visited.
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/About')
	}
]

const router = new VueRouter({
	mode: 'history',
	base: process.env.BASE_URL,
	routes
})

// Reset the UI on page transitions:
// - Enable the search bar
router.beforeEach((to, from, next) => {
	store.dispatch('ui/enableSearch')
	next()
})

export default router
