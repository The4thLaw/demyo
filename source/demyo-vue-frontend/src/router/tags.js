import TagIndex from '@/pages/tags/TagIndex'
import TagView from '@/pages/tags/TagView'

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
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/tags/TagEdit')
	},
	{
		path: '/tags/new',
		name: 'TagAdd',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/tags/TagEdit')
	}
]
