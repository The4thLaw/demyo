import AuthorIndex from '@/pages/authors/AuthorIndex.vue'
import AuthorView from '@/pages/authors/AuthorView.vue'

export default [
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
		path: '/authors/:id/edit',
		name: 'AuthorEdit',
		component: async () => import('@/pages/authors/AuthorEdit.vue')
	},
	{
		path: '/authors/new',
		name: 'AuthorAdd',
		component: async () => import('@/pages/authors/AuthorEdit.vue')
	}
]
