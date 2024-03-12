module.exports = {
	// For the "extends" to work, the stylelint configuration
	// must be in the same directory as the package.json loading the associated package
	extends: [
		'stylelint-config-standard',
		'stylelint-config-recommended-vue'
	],
	
	overrides: [
        {
        	files: ['*.vue', '**/*.vue'],
			rules: {
				indentation: 'tab',
				'block-opening-brace-space-before': 'always',
				// Disable some rules compared to the recommendations
				'declaration-empty-line-before': null,
				'selector-id-pattern': null,
				'selector-class-pattern': null
			}
        }
    ]
}
