export default [
	{
		path: '/manage/export',
		name: 'Export',
		component: () => import('@/pages/manage/ManageExport.vue')
	},

	{
		path: '/manage/import',
		name: 'Import',
		component: () => import('@/pages/manage/ManageImport.vue')
	}
]
