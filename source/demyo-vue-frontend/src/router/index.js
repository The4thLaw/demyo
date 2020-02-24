import Vue from 'vue'
import VueMeta from 'vue-meta'
import VueRouter from 'vue-router'
import store from '@/store/index'
import Home from '@/views/Home.vue'
import authorRoutes from './authors'
import derivativeRoutes from './derivatives'
import derivativeTypeRoutes from './derivativeTypes'
import imageRoutes from './images'
import seriesRoutes from './series'

Vue.use(VueRouter)
Vue.use(VueMeta)

const routes = [
	{
		path: '/',
		name: 'home',
		component: Home
	},
	...authorRoutes,
	...derivativeRoutes,
	...derivativeTypeRoutes,
	...imageRoutes,
	...seriesRoutes,
	{
		path: '/about',
		name: 'about',
		// route level code-splitting
		// this generates a separate chunk (about.[hash].js) for this route
		// which is lazy-loaded when the route is visited.
		component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
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
