/* eslint-disable @typescript-eslint/explicit-function-return-type */
import ReaderFavourites from '@/pages/readers/ReaderFavourites.vue'
import ReaderIndex from '@/pages/readers/ReaderIndex.vue'
import ReaderView from '@/pages/readers/ReaderView.vue'
import ReadingList from '@/pages/readers/ReadingList.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/readers',
		name: 'ReaderIndex',
		component: ReaderIndex as Component
	},
	{
		path: '/readers/:id/view',
		alias: '/readers/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'ReaderView',
		component: ReaderView as Component
	},
	{
		path: '/readers/:id/edit',
		name: 'ReaderEdit',
		component: async () => import('@/pages/readers/ReaderEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/readers/new',
		name: 'ReaderAdd',
		component: async () => import('@/pages/readers/ReaderEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/readers/:id/favourites',
		name: 'ReaderFavourites',
		component: ReaderFavourites as Component
	},
	{
		path: '/readers/:id/readingList',
		name: 'ReadingList',
		component: ReadingList as Component
	},
	{
		path: '/readers/:id/configuration',
		name: 'ReaderConfig',
		component: async () => import('@/pages/readers/ReaderConfig.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
