import { FlatCompat } from '@eslint/eslintrc'
import js from '@eslint/js'
import tsParser from '@typescript-eslint/parser'
import globals from 'globals'
import path from 'path'
import tseslint from 'typescript-eslint'
import { fileURLToPath } from 'url'
import vueParser from 'vue-eslint-parser'

// mimic CommonJS variables
const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

const compat = new FlatCompat({
	baseDirectory: __dirname
})

const commonRules = {
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
	'operator-linebreak': ['error', 'before'],
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
	}],
	// Vuetify uses them
	'vue/no-v-model-argument': 'off'
}

export default [
	{
		ignores: [
			'node_modules/',
			'dist/',
			'target',
			// Generated files
			'auto-imports.d.ts',
			'components.d.ts'
		]
	},

	js.configs.recommended,
	...tseslint.configs.recommended,
	// See https://github.com/vuejs/eslint-plugin-vue/issues/1291
	...compat.extends('plugin:vue/essential'),
	...compat.extends('plugin:vue/recommended'),
	// Note that the following fails with the TypeScript parser
	// ...compat.extends('plugin:vuetify/base'),
	// https://github.com/cypress-io/eslint-plugin-cypress/issues/146
	...compat.extends('plugin:cypress/recommended'),
	// No issue to support flat configs for these two
	...compat.extends('@vue/standard'),
	...compat.extends('plugin:you-dont-need-lodash-underscore/all'),

	{
		files: [
			'**/*.js',
			'**/*.cjs',
			'**/*.ts',
			'**/*.mjs'
		],

		languageOptions: {
			globals: {
				...globals.browser,
				// Vue + unplugin-auto-import
				ref: false
			},
			parser: tsParser
		},

		rules: commonRules
	},

	{
		files: [
			'**/*.vue'
		],

		languageOptions: {
			globals: {
				...globals.browser,
				// Vue + unplugin-auto-import
				ref: false,
				computed: false,
				useSlots: false
			},
			//
			parser: vueParser,
			parserOptions: {
				// See https://eslint.vuejs.org/user-guide/#how-to-use-a-custom-parser
				parser: tsParser
			}
		},

		rules: {
			...commonRules,
			// See https://github.com/vuejs/language-tools/issues/47
			'no-unused-vars': 'off',
			'@typescript-eslint/no-unused-vars': 'off',
			// Broken in conjunction with tsParser
			'vue/valid-v-for': 'off'
		}
	},

	{
		files: [
			'**/__tests__/*.{j,t}s?(x)',
			'**/tests/unit/**/*.spec.{j,t}s?(x)'
		],

		languageOptions: {
			globals: {
				...globals.jest
			}
		}
	},

	{
		files: [
			'cypress/**/*.js'
		],

		languageOptions: {
			globals: {
				...globals.mocha
			}
		}
	}
]
