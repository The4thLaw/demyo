import ReaderFavourites from '@/views/readers/ReaderFavourites'
import ReadingList from '@/views/readers/ReadingList'
import SeriesEdit from '@/views/series/SeriesEdit'
import SeriesIndex from '@/views/series/SeriesIndex'
import SeriesView from '@/views/series/SeriesView'

export default [
	{
		// TODO: implement this
		path: '/readers',
		name: 'ReaderIndex',
		component: SeriesIndex
	},
	{
		// TODO: implement this
		path: '/readers/:id/view',
		alias: '/readers/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'ReaderView',
		component: SeriesView
	},
	{
		// TODO: implement this
		path: '/readers/:id/edit',
		name: 'ReaderEdit',
		component: SeriesEdit
	},
	{
		// TODO: implement this
		path: '/readers/new',
		name: 'ReaderAdd',
		component: SeriesEdit
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
	}
]
