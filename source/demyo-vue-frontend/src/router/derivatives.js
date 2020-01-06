import DerivativeEdit from '@/views/derivatives/DerivativeEdit'
import DerivativeIndex from '@/views/derivatives/DerivativeIndex'

export default [
	{
		path: '/derivatives',
		name: 'DerivativeIndex',
		component: DerivativeIndex
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
