import ReaderConfig from '@/views/readers/ReaderConfig'
import ReaderEdit from '@/views/readers/ReaderEdit'
import ReaderFavourites from '@/views/readers/ReaderFavourites'
import ReaderIndex from '@/views/readers/ReaderIndex'
import ReadingList from '@/views/readers/ReadingList'
import ReaderView from '@/views/readers/ReaderView'

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
		component: ReaderEdit
	},
	{
		path: '/readers/new',
		name: 'ReaderAdd',
		component: ReaderEdit
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
		component: ReaderConfig
	}
]
