import CollectionView from '@/pages/collections/CollectionView'

export default [
	{
		path: '/collections/:id/view',
		alias: '/collections/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'CollectionView',
		component: CollectionView
	},
	{
		path: '/collections/:id/edit',
		name: 'CollectionEdit',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/collections/CollectionEdit')
	},
	{
		path: '/collections/new',
		name: 'CollectionAdd',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/collections/CollectionEdit')
	}
]
