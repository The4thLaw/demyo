module.exports = {
	// For the "extends" to work, the stylelint configuration
	// must be in the same directory as the package.json loading the associated package
	extends: 'stylelint-config-standard',
	rules: {
		'declaration-empty-line-before': null,
		'indentation': 'tab',
		'block-opening-brace-space-before': 'always'
	}
}
