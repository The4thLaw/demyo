/* eslint-disable @typescript-eslint/explicit-function-return-type */
import ImageIndex from '@/pages/images/ImageIndex.vue'
import ImageView from '@/pages/images/ImageView.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/images',
		name: 'ImageIndex',
		component: ImageIndex as Component
	},
	{
		path: '/images/:id/view',
		alias: '/images/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'ImageView',
		component: ImageView as Component
	},
	{
		path: '/images/:id/edit',
		name: 'ImageEdit',
		component: async () => import('@/pages/images/ImageEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/images/new',
		name: 'ImageDetect',
		component: async () => import('@/pages/images/ImageDetect.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
