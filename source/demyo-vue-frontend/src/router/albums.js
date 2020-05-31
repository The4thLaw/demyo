import AuthorEdit from '@/views/authors/AuthorEdit'
import AlbumIndex from '@/views/albums/AlbumIndex'
import AuthorView from '@/views/authors/AuthorView'

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
		component: AuthorView
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
