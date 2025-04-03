/* eslint-disable @typescript-eslint/explicit-function-return-type */
import AuthorIndex from '@/pages/authors/AuthorIndex.vue'
import AuthorView from '@/pages/authors/AuthorView.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/authors',
		name: 'AuthorIndex',
		component: AuthorIndex as Component
	},
	{
		path: '/authors/:id/view',
		alias: '/authors/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'AuthorView',
		component: AuthorView as Component
	},
	{
		path: '/authors/:id/edit',
		name: 'AuthorEdit',
		component: async () => import('@/pages/authors/AuthorEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/authors/new',
		name: 'AuthorAdd',
		component: async () => import('@/pages/authors/AuthorEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
