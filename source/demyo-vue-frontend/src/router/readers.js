import ReaderFavourites from '@/pages/readers/ReaderFavourites'
import ReaderIndex from '@/pages/readers/ReaderIndex'
import ReadingList from '@/pages/readers/ReadingList'
import ReaderView from '@/pages/readers/ReaderView'

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
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/readers/ReaderEdit')
	},
	{
		path: '/readers/new',
		name: 'ReaderAdd',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/readers/ReaderEdit')
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
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/readers/ReaderConfig')
	}
]
