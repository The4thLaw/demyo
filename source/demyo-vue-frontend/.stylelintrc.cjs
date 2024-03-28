const commonRules = {
	'@stylistic/indentation': 'tab',
	'@stylistic/block-opening-brace-space-before': 'always',
	// Disable some rules compared to the recommendations
	'declaration-empty-line-before': null,
	'selector-id-pattern': null,
	'selector-class-pattern': null
}

module.exports = {
	plugins: [
		'@stylistic/stylelint-plugin',
		'stylelint-scss'
	],

	// For the "extends" to work, the stylelint configuration
	// must be in the same directory as the package.json loading the associated package
	extends: [
		'stylelint-config-standard',
		'stylelint-config-recommended-vue'
	],

	overrides: [
		{
			files: ['*.vue', '**/*.vue'],
			// TODO: Vue 3: migrate from LESS to SCSS then enable this
			// extends: 'stylelint-config-standard-scss',
			rules: {
				...commonRules,
				'import-notation': null
			}
		},

		{
			files: ['**/*.less'],
			customSyntax: 'postcss-less',
			rules: commonRules
		},

		{
			files: ['**/*.scss'],
			extends: 'stylelint-config-standard-scss',
			rules: commonRules
		}
	]
}
