import AuthorIndex from '@/pages/authors/AuthorIndex'
import AuthorView from '@/pages/authors/AuthorView'

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
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/authors/AuthorEdit')
	},
	{
		path: '/authors/new',
		name: 'AuthorAdd',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/authors/AuthorEdit')
	}
]
