export default [
	{
		path: '/manage/export',
		name: 'Export',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/manage/Export')
	},

	{
		path: '/manage/import',
		name: 'Import',
		component: () => import(/* webpackChunkName: "manage" */ '@/pages/manage/Import')
	}
]
