/* eslint-disable @typescript-eslint/explicit-function-return-type */
import TagIndex from '@/pages/tags/TagIndex.vue'
import TagView from '@/pages/tags/TagView.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/tags',
		name: 'TagIndex',
		component: TagIndex as Component
	},
	{
		path: '/tags/:id/view',
		alias: '/tags/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'TagView',
		component: TagView as Component
	},
	{
		path: '/tags/:id/edit',
		name: 'TagEdit',
		component: async () => import('@/pages/tags/TagEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/tags/new',
		name: 'TagAdd',
		component: async () => import('@/pages/tags/TagEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
