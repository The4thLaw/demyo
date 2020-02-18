import AuthorEdit from '@/views/authors/AuthorEdit'
import DerivativeTypeIndex from '@/views/derivativeTypes/DerivativeTypeIndex'
import DerivativeTypeView from '@/views/derivativeTypes/DerivativeTypeView'

export default [
	{
		path: '/derivativeTypes',
		name: 'DerivativeTypeIndex',
		component: DerivativeTypeIndex
	},
	{
		path: '/derivativeTypes/:id/view',
		alias: '/derivativeTypes/view/:id', // Kept for backwards compatibility with Demyo 2.0, 2.1
		name: 'DerivativeTypeView',
		component: DerivativeTypeView
	},
	{
		path: '/authors/:id/edit',
		name: 'AuthorEdit',
		component: AuthorEdit
	},
	{
		path: '/authors/new',
		name: 'AuthorAdd',
		component: AuthorEdit
	}
]
