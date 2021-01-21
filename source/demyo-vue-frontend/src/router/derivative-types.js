import DerivativeTypeIndex from '@/pages/derivativeTypes/DerivativeTypeIndex'
import DerivativeTypeView from '@/pages/derivativeTypes/DerivativeTypeView'

export default [
	{
		path: '/derivativeTypes',
		name: 'DerivativeTypeIndex',
		component: DerivativeTypeIndex
	},
	{
		path: '/derivativeTypes/:id/view',
		alias: '/derivativeTypes/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'DerivativeTypeView',
		component: DerivativeTypeView
	},
	{
		path: '/derivativeTypes/:id/edit',
		name: 'DerivativeTypeEdit',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/derivativeTypes/DerivativeTypeEdit')
	},
	{
		path: '/derivativeTypes/new',
		name: 'DerivativeTypeAdd',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/derivativeTypes/DerivativeTypeEdit')
	}
]
