import DerivativeTypeIndex from '@/pages/derivativeTypes/DerivativeTypeIndex.vue'
import DerivativeTypeView from '@/pages/derivativeTypes/DerivativeTypeView.vue'

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
		component: async () => import('@/pages/derivativeTypes/DerivativeTypeEdit.vue')
	},
	{
		path: '/derivativeTypes/new',
		name: 'DerivativeTypeAdd',
		component: async () => import('@/pages/derivativeTypes/DerivativeTypeEdit.vue')
	}
]
