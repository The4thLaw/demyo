/* eslint-disable @typescript-eslint/explicit-function-return-type */
import BindingIndex from '@/pages/bindings/BindingIndex.vue'
import BindingView from '@/pages/bindings/BindingView.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/bindings',
		name: 'BindingIndex',
		component: BindingIndex as Component
	},
	{
		path: '/bindings/:id/view',
		alias: '/bindings/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'BindingView',
		component: BindingView as Component
	},
	{
		path: '/bindings/:id/edit',
		name: 'BindingEdit',
		component: async () => import('@/pages/bindings/BindingEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/bindings/new',
		name: 'BindingAdd',
		component: async () => import('@/pages/bindings/BindingEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
