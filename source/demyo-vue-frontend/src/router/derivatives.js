import MinimalLayout from '@/layouts/MinimalLayout'
import DerivativeIndex from '@/pages/derivatives/DerivativeIndex'
import DerivativeStickers from '@/pages/derivatives/DerivativeStickers'
import DerivativeView from '@/pages/derivatives/DerivativeView'

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
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/derivatives/DerivativeEdit')
	},
	{
		path: '/derivatives/new',
		name: 'DerivativeAdd',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/derivatives/DerivativeEdit')
	}
]
