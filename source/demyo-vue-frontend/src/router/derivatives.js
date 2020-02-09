import DerivativeEdit from '@/views/derivatives/DerivativeEdit'
import DerivativeIndex from '@/views/derivatives/DerivativeIndex'
import DerivativeView from '@/views/derivatives/DerivativeView'

export default [
	{
		path: '/derivatives',
		name: 'DerivativeIndex',
		component: DerivativeIndex
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
		component: DerivativeEdit
	},
	{
		path: '/derivatives/new',
		name: 'DerivativeAdd',
		component: DerivativeEdit
	}
]
