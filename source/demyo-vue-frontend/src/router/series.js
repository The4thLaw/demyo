import SeriesIndex from '@/pages/series/SeriesIndex'
import SeriesView from '@/pages/series/SeriesView'

export default [
	{
		// This is no longer used but remains for now, in case I want to add it back
		// after end-user testing
		path: '/series',
		name: 'SeriesIndex',
		component: SeriesIndex
	},
	{
		path: '/series/:id/view',
		alias: '/series/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'SeriesView',
		component: SeriesView
	},
	{
		path: '/series/:id/edit',
		name: 'SeriesEdit',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/series/SeriesEdit')
	},
	{
		path: '/series/new',
		name: 'SeriesAdd',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/series/SeriesEdit')
	}
]
