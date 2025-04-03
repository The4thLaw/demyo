/* eslint-disable @typescript-eslint/explicit-function-return-type */
import DerivativeSourceIndex from '@/pages/derivativeSources/DerivativeSourceIndex.vue'
import DerivativeSourceView from '@/pages/derivativeSources/DerivativeSourceView.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/derivativeSources',
		name: 'DerivativeSourceIndex',
		component: DerivativeSourceIndex as Component
	},
	{
		path: '/derivativeSources/:id/view',
		alias: '/derivativeSources/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'DerivativeSourceView',
		component: DerivativeSourceView as Component
	},
	{
		path: '/derivativeSources/:id/edit',
		name: 'DerivativeSourceEdit',
		component: async () =>
			import('@/pages/derivativeSources/DerivativeSourceEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/derivativeSources/new',
		name: 'DerivativeSourceAdd',
		component: async () =>
			import('@/pages/derivativeSources/DerivativeSourceEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
