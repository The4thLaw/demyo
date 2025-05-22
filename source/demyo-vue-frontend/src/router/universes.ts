/* eslint-disable @typescript-eslint/explicit-function-return-type */
import UniverseIndex from '@/pages/universes/UniverseIndex.vue'
import UniverseView from '@/pages/universes/UniverseView.vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/universes',
		name: 'UniverseIndex',
		component: UniverseIndex as Component
	},
	{
		path: '/universes/:id/view',
		name: 'UniverseView',
		component: UniverseView as Component
	},
	{
		path: '/universes/:id/edit',
		name: 'UniverseEdit',
		component: async () => import('@/pages/universes/UniverseEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/universes/new',
		name: 'UniverseAdd',
		component: async () => import('@/pages/universes/UniverseEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
