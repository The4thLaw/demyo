/* eslint-disable @typescript-eslint/explicit-function-return-type */
import SeriesIndex from '@/pages/series/SeriesIndex.vue'
import SeriesView from '@/pages/series/SeriesView.vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		// This is no longer used but remains for now, in case I want to add it back
		// after end-user testing
		path: '/series',
		name: 'SeriesIndex',
		component: SeriesIndex as Component
	},
	{
		path: '/series/:id/view',
		alias: '/series/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'SeriesView',
		component: SeriesView as Component
	},
	{
		path: '/series/:id/edit',
		name: 'SeriesEdit',
		component: async () => import('@/pages/series/SeriesEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/series/new',
		name: 'SeriesAdd',
		component: async () => import('@/pages/series/SeriesEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
