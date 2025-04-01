import TagIndex from '@/pages/tags/TagIndex.vue'
import TagView from '@/pages/tags/TagView.vue'

export default [
	{
		path: '/tags',
		name: 'TagIndex',
		component: TagIndex
	},
	{
		path: '/tags/:id/view',
		alias: '/tags/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'TagView',
		component: TagView
	},
	{
		path: '/tags/:id/edit',
		name: 'TagEdit',
		component: async () => import('@/pages/tags/TagEdit.vue')
	},
	{
		path: '/tags/new',
		name: 'TagAdd',
		component: async () => import('@/pages/tags/TagEdit.vue')
	}
]
