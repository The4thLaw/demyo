import PublisherEdit from '@/views/publishers/PublisherEdit'
import PublisherIndex from '@/views/publishers/PublisherIndex'
import PublisherView from '@/views/publishers/PublisherView'

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
		component: PublisherEdit
	},
	{
		path: '/publishers/new',
		name: 'PublisherAdd',
		component: PublisherEdit
	}
]
