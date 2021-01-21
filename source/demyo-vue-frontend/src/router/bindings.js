import BindingIndex from '@/pages/bindings/BindingIndex'
import BindingView from '@/pages/bindings/BindingView'

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
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/bindings/BindingEdit')
	},
	{
		path: '/bindings/new',
		name: 'BindingAdd',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/bindings/BindingEdit')
	}
]
