import AuthorEdit from '@/views/authors/AuthorEdit'
import AuthorIndex from '@/views/authors/AuthorIndex'
import AuthorView from '@/views/authors/AuthorView'

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
		component: AuthorEdit
	},
	{
		path: '/authors/new',
		name: 'AuthorAdd',
		component: AuthorEdit
	}
]
