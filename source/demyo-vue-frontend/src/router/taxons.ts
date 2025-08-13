/* eslint-disable @typescript-eslint/explicit-function-return-type */
import TaxonIndex from '@/pages/taxons/TaxonIndex.vue'
import TaxonView from '@/pages/taxons/TaxonView.vue'
import type { Component } from 'vue'
import type { RouteRecordRaw } from 'vue-router'

export default [
	{
		path: '/taxons',
		name: 'TaxonIndex',
		component: TaxonIndex as Component
	},
	{
		path: '/taxons/:id/view',
		name: 'TaxonView',
		component: TaxonView as Component
	},
	{
		path: '/taxons/:id/edit',
		name: 'TaxonEdit',
		component: async () => import('@/pages/taxons/TaxonEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/genres/new',
		name: 'GenreAdd',
		props: {
			type: 'GENRE' satisfies TaxonType
		},
		component: async () => import('@/pages/taxons/TaxonEdit.vue') as unknown as Promise<Component>
	},
	{
		path: '/tags/new',
		name: 'TagAdd',
		props: {
			type: 'TAG' satisfies TaxonType
		},
		component: async () => import('@/pages/taxons/TaxonEdit.vue') as unknown as Promise<Component>
	}
] satisfies RouteRecordRaw[]
