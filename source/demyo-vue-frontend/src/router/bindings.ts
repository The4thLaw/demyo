import BindingIndex from '@/pages/bindings/BindingIndex.vue'
import BindingView from '@/pages/bindings/BindingView.vue'

export default [
	{
		path: '/bindings',
		name: 'BindingIndex',
		component: BindingIndex
	},
	{
		path: '/bindings/:id/view',
		alias: '/bindings/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'BindingView',
		component: BindingView
	},
	{
		path: '/bindings/:id/edit',
		name: 'BindingEdit',
		component: async () => import('@/pages/bindings/BindingEdit.vue')
	},
	{
		path: '/bindings/new',
		name: 'BindingAdd',
		component: async () => import('@/pages/bindings/BindingEdit.vue')
	}
]
