/* eslint-disable @typescript-eslint/explicit-function-return-type */
import PublisherIndex from '@/pages/publishers/PublisherIndex.vue'
import PublisherView from '@/pages/publishers/PublisherView.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/publishers',
		name: 'PublisherIndex',
		component: PublisherIndex as Component
	},
	{
		path: '/publishers/:id/view',
		alias: '/publishers/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'PublisherView',
		component: PublisherView as Component
	},
	{
		path: '/publishers/:id/edit',
		name: 'PublisherEdit',
		component: async () => import('@/pages/publishers/PublisherEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/publishers/new',
		name: 'PublisherAdd',
		component: async () => import('@/pages/publishers/PublisherEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
