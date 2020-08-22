import AuthorEdit from '@/views/authors/AuthorEdit'
import TagIndex from '@/views/tags/TagIndex'
import AuthorView from '@/views/authors/AuthorView'

export default [
	{
		path: '/tags',
		name: 'TagIndex',
		component: TagIndex
	},
	{
		path: '/tags/:id/view',
		alias: '/tags/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'TagView',
		component: AuthorView
	},
	{
		path: '/tags/:id/edit',
		name: 'TagEdit',
		component: AuthorEdit
	},
	{
		path: '/tags/new',
		name: 'TagAdd',
		component: AuthorEdit
	}
]
