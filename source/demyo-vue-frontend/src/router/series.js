import AuthorEdit from '@/views/authors/AuthorEdit'
import SeriesIndex from '@/views/series/SeriesIndex'
import AuthorView from '@/views/authors/AuthorView'

export default [
	{
		path: '/series',
		name: 'SeriesIndex',
		component: SeriesIndex
	},
	{
		path: '/series/:id/view',
		alias: '/series/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'SeriesView',
		component: AuthorView
	},
	{
		path: '/series/:id/edit',
		name: 'SeriesEdit',
		component: AuthorEdit
	},
	{
		path: '/series/new',
		name: 'SeriesAdd',
		component: AuthorEdit
	}
]
