import MinimalLayout from '@/layouts/MinimalLayout.vue'
import DerivativeIndex from '@/pages/derivatives/DerivativeIndex.vue'
import DerivativeStickers from '@/pages/derivatives/DerivativeStickers.vue'
import DerivativeView from '@/pages/derivatives/DerivativeView.vue'

export default [
	{
		path: '/derivatives',
		name: 'DerivativeIndex',
		component: DerivativeIndex
	},
	{
		path: '/derivatives/stickers',
		name: 'DerivativeStickers',
		component: DerivativeStickers,
		meta: {
			layout: MinimalLayout
		}
	},
	{
		path: '/derivatives/:id/view',
		alias: '/derivatives/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'DerivativeView',
		component: DerivativeView
	},
	{
		path: '/derivatives/:id/edit',
		name: 'DerivativeEdit',
		component: () => import('@/pages/derivatives/DerivativeEdit.vue')
	},
	{
		path: '/derivatives/new',
		name: 'DerivativeAdd',
		component: () => import('@/pages/derivatives/DerivativeEdit.vue')
	}
]
