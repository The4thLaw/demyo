import ImageEdit from '@/views/images/ImageEdit'
import ImageIndex from '@/views/images/ImageIndex'
import ImageView from '@/views/images/ImageView'

export default [
	{
		path: '/images',
		name: 'ImageIndex',
		component: ImageIndex
	},
	{
		path: '/images/:id/view',
		alias: '/images/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'ImageView',
		component: ImageView
	},
	{
		path: '/images/:id/edit',
		name: 'ImageEdit',
		component: ImageEdit
	}
]
