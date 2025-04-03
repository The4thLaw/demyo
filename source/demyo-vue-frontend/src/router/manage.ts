/* eslint-disable @typescript-eslint/explicit-function-return-type */
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/manage/export',
		name: 'Export',
		component: async () => import('@/pages/manage/ManageExport.vue') as unknown as Promise<Component>
	},

	{
		path: '/manage/import',
		name: 'Import',
		component: async () => import('@/pages/manage/ManageImport.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
