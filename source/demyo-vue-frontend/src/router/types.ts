import TypeIndex from '@/pages/types/TypeIndex.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/types',
		name: 'TypeIndex',
		component: TypeIndex as Component
	}
] satisfies RouteRecordRaw[]
