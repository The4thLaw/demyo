import DerivativeSourceIndex from '@/pages/derivativeSources/DerivativeSourceIndex'
import DerivativeSourceView from '@/pages/derivativeSources/DerivativeSourceView'

export default [
	{
		path: '/derivativeSources',
		name: 'DerivativeSourceIndex',
		component: DerivativeSourceIndex
	},
	{
		path: '/derivativeSources/:id/view',
		alias: '/derivativeSources/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'DerivativeSourceView',
		component: DerivativeSourceView
	},
	{
		path: '/derivativeSources/:id/edit',
		name: 'DerivativeSourceEdit',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/derivativeSources/DerivativeSourceEdit')
	},
	{
		path: '/derivativeSources/new',
		name: 'DerivativeSourceAdd',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/derivativeSources/DerivativeSourceEdit')
	}
]
