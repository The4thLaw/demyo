export default [
	{
		path: '/manage/export',
		name: 'Export',
		component: () => import('@/pages/manage/Export.vue')
	},

	{
		path: '/manage/import',
		name: 'Import',
		component: () => import('@/pages/manage/Import.vue')
	}
]
