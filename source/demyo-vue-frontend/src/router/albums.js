import AlbumIndex from '@/pages/albums/AlbumIndex'
import AlbumView from '@/pages/albums/AlbumView'

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
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/albums/AlbumEdit')
	},
	{
		path: '/albums/new',
		name: 'AlbumAdd',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/albums/AlbumEdit')
	}
]
