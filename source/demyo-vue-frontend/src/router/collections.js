import CollectionEdit from '@/pages/collections/CollectionEdit'
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
		component: CollectionEdit
	},
	{
		path: '/collections/new',
		name: 'CollectionAdd',
		component: CollectionEdit
	}
]
