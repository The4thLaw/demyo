import AuthorEdit from '@/views/authors/AuthorEdit'
import AlbumIndex from '@/views/albums/AlbumIndex'
import AlbumView from '@/views/albums/AlbumView'

export default [
	{
		path: '/albums',
		name: 'AlbumIndex',
		component: AlbumIndex
	},
	{
		path: '/albums/:id/view',
		alias: '/albums/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'AlbumView',
		component: AlbumView
	},
	{
		path: '/albums/:id/edit',
		name: 'AlbumEdit',
		component: AuthorEdit
	},
	{
		path: '/albums/new',
		name: 'AlbumAdd',
		component: AuthorEdit
	}
]
