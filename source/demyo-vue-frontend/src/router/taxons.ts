/* eslint-disable @typescript-eslint/explicit-function-return-type */
import TagIndex from '@/pages/tags/TagIndex.vue'
import TaxonView from '@/pages/taxons/TaxonView.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/taxons',
		name: 'TaxonIndex',
		component: TagIndex as Component
	},
	{
		path: '/taxons/:id/view',
		name: 'TaxonView',
		component: TaxonView as Component
	},
	{
		path: '/taxons/:id/edit',
		name: 'TaxonEdit',
		component: async () => import('@/pages/tags/TagEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/taxons/new',
		name: 'TaxonAdd',
		component: async () => import('@/pages/tags/TagEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
