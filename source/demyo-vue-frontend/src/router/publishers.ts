import PublisherIndex from '@/pages/publishers/PublisherIndex.vue'
import PublisherView from '@/pages/publishers/PublisherView.vue'

export default [
	{
		path: '/publishers',
		name: 'PublisherIndex',
		component: PublisherIndex
	},
	{
		path: '/publishers/:id/view',
		alias: '/publishers/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'PublisherView',
		component: PublisherView
	},
	{
		path: '/publishers/:id/edit',
		name: 'PublisherEdit',
		component: () => import('@/pages/publishers/PublisherEdit.vue')
	},
	{
		path: '/publishers/new',
		name: 'PublisherAdd',
		component: () => import('@/pages/publishers/PublisherEdit.vue')
	}
]
