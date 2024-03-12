import SeriesIndex from '@/pages/series/SeriesIndex.vue'
import SeriesView from '@/pages/series/SeriesView.vue'

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
		component: () => import('@/pages/series/SeriesEdit.vue')
	},
	{
		path: '/series/new',
		name: 'SeriesAdd',
		component: () => import('@/pages/series/SeriesEdit.vue')
	}
]
