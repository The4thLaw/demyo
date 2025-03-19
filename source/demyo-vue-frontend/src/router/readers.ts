import ReaderFavourites from '@/pages/readers/ReaderFavourites.vue'
import ReaderIndex from '@/pages/readers/ReaderIndex.vue'
import ReaderView from '@/pages/readers/ReaderView.vue'
import ReadingList from '@/pages/readers/ReadingList.vue'

export default [
	{
		path: '/readers',
		name: 'ReaderIndex',
		component: ReaderIndex
	},
	{
		path: '/readers/:id/view',
		alias: '/readers/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'ReaderView',
		component: ReaderView
	},
	{
		path: '/readers/:id/edit',
		name: 'ReaderEdit',
		component: () => import('@/pages/readers/ReaderEdit.vue')
	},
	{
		path: '/readers/new',
		name: 'ReaderAdd',
		component: () => import('@/pages/readers/ReaderEdit.vue')
	},
	{
		path: '/readers/:id/favourites',
		name: 'ReaderFavourites',
		component: ReaderFavourites
	},
	{
		path: '/readers/:id/readingList',
		name: 'ReadingList',
		component: ReadingList
	},
	{
		path: '/readers/:id/configuration',
		name: 'ReaderConfig',
		component: () => import('@/pages/readers/ReaderConfig.vue')
	}
]
