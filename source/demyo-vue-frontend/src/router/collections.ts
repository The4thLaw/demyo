/* eslint-disable @typescript-eslint/explicit-function-return-type */
import CollectionView from '@/pages/collections/CollectionView.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/collections/:id/view',
		alias: '/collections/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'CollectionView',
		component: CollectionView as Component
	},
	{
		path: '/collections/:id/edit',
		name: 'CollectionEdit',
		component: async () => import('@/pages/collections/CollectionEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/collections/new',
		name: 'CollectionAdd',
		component: async () => import('@/pages/collections/CollectionEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
