/* eslint-disable @typescript-eslint/explicit-function-return-type */
import AlbumIndex from '@/pages/albums/AlbumIndex.vue'
import AlbumView from '@/pages/albums/AlbumView.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/albums',
		name: 'AlbumIndex',
		component: AlbumIndex as Component
	},
	{
		path: '/albums/:id/view',
		alias: '/albums/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'AlbumView',
		component: AlbumView as Component
	},
	{
		path: '/albums/:id/edit',
		name: 'AlbumEdit',
		component: async () => import('@/pages/albums/AlbumEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/albums/new',
		name: 'AlbumAdd',
		component: async () => import('@/pages/albums/AlbumEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
