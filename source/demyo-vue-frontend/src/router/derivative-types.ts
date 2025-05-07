/* eslint-disable @typescript-eslint/explicit-function-return-type */
import DerivativeTypeView from '@/pages/derivativeTypes/DerivativeTypeView.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/derivativeTypes/:id/view',
		alias: '/derivativeTypes/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'DerivativeTypeView',
		component: DerivativeTypeView as Component
	},
	{
		path: '/derivativeTypes/:id/edit',
		name: 'DerivativeTypeEdit',
		component: async () => import('@/pages/derivativeTypes/DerivativeTypeEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/derivativeTypes/new',
		name: 'DerivativeTypeAdd',
		component: async () => import('@/pages/derivativeTypes/DerivativeTypeEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
