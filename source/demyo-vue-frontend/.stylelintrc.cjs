const commonRules = {
	'@stylistic/indentation': 'tab',
	'@stylistic/block-opening-brace-space-before': 'always',
	// Disable some rules compared to the recommendations
	'declaration-empty-line-before': null,
	'selector-id-pattern': null,
	'selector-class-pattern': null,
	// Conflicts with SASS
	'import-notation': null,
	// SCSS has a similar one
	'at-rule-no-unknown': null
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
		'stylelint-config-recommended-vue',
		'stylelint-config-standard-scss'
	],

	overrides: [
		{
			files: ['*.vue', '**/*.vue'],
			customSyntax: 'postcss-html',
			rules: {
				...commonRules
			}
		},

		{
			files: ['**/*.scss'],
			rules: commonRules
		}
	],

	ignoreFiles: [
		'src/assets/*.css'
	]
}
