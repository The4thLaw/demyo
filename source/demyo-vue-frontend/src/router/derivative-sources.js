import DerivativeSourceIndex from '@/pages/derivativeSources/DerivativeSourceIndex.vue'
import DerivativeSourceView from '@/pages/derivativeSources/DerivativeSourceView.vue'

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
		component: () => import('@/pages/derivativeSources/DerivativeSourceEdit.vue')
	},
	{
		path: '/derivativeSources/new',
		name: 'DerivativeSourceAdd',
		component: () => import('@/pages/derivativeSources/DerivativeSourceEdit.vue')
	}
]
