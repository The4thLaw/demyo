import TypeManagement from '@/pages/types/TypeManagement.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/types',
		name: 'TypeManagement',
		component: TypeManagement as Component
	}
] satisfies RouteRecordRaw[]
