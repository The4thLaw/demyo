import ImageIndex from '@/pages/images/ImageIndex.vue'
import ImageView from '@/pages/images/ImageView.vue'

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
		component: () => import('@/pages/images/ImageEdit.vue')
	},
	{
		path: '/images/new',
		name: 'ImageDetect',
		component: () => import('@/pages/images/ImageDetect.vue')
	}
]
