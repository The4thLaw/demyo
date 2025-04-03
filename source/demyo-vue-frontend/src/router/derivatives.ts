/* eslint-disable @typescript-eslint/explicit-function-return-type */
import MinimalLayout from '@/layouts/MinimalLayout.vue'
import DerivativeIndex from '@/pages/derivatives/DerivativeIndex.vue'
import DerivativeStickers from '@/pages/derivatives/DerivativeStickers.vue'
import DerivativeView from '@/pages/derivatives/DerivativeView.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/derivatives',
		name: 'DerivativeIndex',
		component: DerivativeIndex as Component
	},
	{
		path: '/derivatives/stickers',
		name: 'DerivativeStickers',
		component: DerivativeStickers as Component,
		meta: {
			layout: MinimalLayout
		}
	},
	{
		path: '/derivatives/:id/view',
		alias: '/derivatives/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'DerivativeView',
		component: DerivativeView as Component
	},
	{
		path: '/derivatives/:id/edit',
		name: 'DerivativeEdit',
		component: async () => import('@/pages/derivatives/DerivativeEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/derivatives/new',
		name: 'DerivativeAdd',
		component: async () => import('@/pages/derivatives/DerivativeEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
