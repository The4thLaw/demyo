/* eslint-disable @typescript-eslint/explicit-function-return-type */
import BookTypeView from '@/pages/bookTypes/BookTypeView.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/bookTypes/:id/view',
		name: 'BookTypeView',
		component: BookTypeView as Component
	},
	{
		path: '/bookTypes/:id/edit',
		name: 'BookTypeEdit',
		component: async () => import('@/pages/bookTypes/BookTypeEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/bookTypes/new',
		name: 'BookTypeAdd',
		component: async () => import('@/pages/bookTypes/BookTypeEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
