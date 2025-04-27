/* eslint-disable @typescript-eslint/explicit-function-return-type */
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/bookTypes/:id/edit',
		name: 'BookTypeEdit',
		component: async () => import('@/pages/bookTypes/BookTypeEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
