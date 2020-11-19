module.exports = {
	root: true,
	env: {
		node: true,
		browser: true
	},
	parserOptions: {
		parser: 'babel-eslint'
	},
	extends: [
		'eslint:recommended',
		'plugin:vue/essential',
		'@vue/standard',
		'plugin:vue/recommended'
	],
	rules: {
		// Possible errors
		// Logging to the console is OK in some cases
		'no-console': 'off',

		// Best practices
		'consistent-return': 'error',
		curly: 'error',
		'default-case': 'warn',
		'dot-location': ['warn', 'property'],
		eqeqeq: ['error', 'smart'],
		'no-alert': 'error',
		'no-caller': 'error',
		'no-else-return': 'error',
		'no-eval': 'error',
		'no-extra-bind': 'error',
		'no-implicit-globals': 'error',
		'no-implied-eval': 'error',
		'no-invalid-this': 'error',
		'no-iterator': 'error',
		'no-multi-spaces': 'warn',
		'no-multi-str': 'error',
		'no-proto': 'error',
		'no-return-await': 'error',
		'no-script-url': 'error',
		'no-sequences': 'error',
		'no-throw-literal': 'error',
		'no-unused-expressions': 'error',
		'no-useless-call': 'error',
		// 'no-useless-catch': 'error', // v5.11
		'no-useless-concat': 'error',
		'no-useless-return': 'error',
		radix: 'warn',
		'require-await': 'error',
		'wrap-iife': 'error',

		// Style
		'block-spacing': 'error',
		indent: ['error', 'tab'],
		'max-len': ['error', {
			code: 120
		}],
		'no-tabs': 'off',
		'no-mixed-spaces-and-tabs': ['error', 'smart-tabs'],
		'space-before-blocks': 'error',
		'space-before-function-paren': ['error', {
			anonymous: 'always',
			named: 'never',
			asyncArrow: 'always'
		}],

		// Vue
		'vue/html-indent': ['error', 'tab'],
		'vue/max-attributes-per-line': ['error', {
			singleline: 4,
			multiline: 4
		}]
	}
}
