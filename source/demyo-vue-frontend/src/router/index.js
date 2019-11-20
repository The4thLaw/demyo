import Vue from 'vue'
import VueMeta from 'vue-meta'
import VueRouter from 'vue-router'
import store from '@/store/index'
import Home from '@/views/Home.vue'
import AuthorIndex from '@/views/authors/AuthorIndex'
import AuthorView from '@/views/authors/AuthorView'

Vue.use(VueRouter)
Vue.use(VueMeta)

const routes = [
	{
		path: '/',
		name: 'home',
		component: Home
	},
	{
		path: '/authors',
		name: 'AuthorIndex',
		component: AuthorIndex
	},
	{
		path: '/authors/:id/view',
		alias: '/authors/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'AuthorView',
		component: AuthorView
	},
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
// - TODO: clear the quick tasks
router.beforeEach((to, from, next) => {
	store.dispatch('ui/enableSearch')
	next()
})

export default router
