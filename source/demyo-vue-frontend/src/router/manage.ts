export default [
	{
		path: '/manage/export',
		name: 'Export',
		component: async () => import('@/pages/manage/ManageExport.vue')
	},

	{
		path: '/manage/import',
		name: 'Import',
		component: async () => import('@/pages/manage/ManageImport.vue')
	}
]
