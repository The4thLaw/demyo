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
	// Note: require-await is off because TypeScript offers a better alternative

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
	'wrap-iife': 'error',
	// Used to explicitely mark promises as not awaited
	'no-void': ['error', { allowAsStatement: true }],

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
	'vue/no-v-model-argument': 'off',

	// TypeScript tweaks
	'@typescript-eslint/prefer-destructuring': ['error', { array: false }],
	'@typescript-eslint/max-params': ['error', 5],
	// TypeScript warnings we don't really care about
	'@typescript-eslint/no-confusing-void-expression': 'off',
	'@typescript-eslint/no-magic-numbers': 'off',
	'@typescript-eslint/prefer-readonly-parameter-types': 'off',
	'@typescript-eslint/init-declarations': 'off'
}

const allTsRules = tseslint.configs.all
	.flatMap(c => Object.keys(c.rules || {}))
	.filter(r => r.startsWith('@typescript-eslint'))
const tsRulesOff = {}
allTsRules.forEach(r => { tsRulesOff[r] = 'off' })

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
	...tseslint.configs.all,
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

	// The following features a super convoluted way of tailoring the TypeScript rules to specific cases
	{
		files: [
			'**/*.js',
			'**/*.cjs',
			'**/*.mjs'
		],

		languageOptions: {
			globals: {
				...globals.browser,
				// Vue + unplugin-auto-import
				ref: false
			}
		},

		rules: {
			...commonRules,
			...tsRulesOff
		}
	},

	{
		files: [
			'**/*.ts'
		],

		languageOptions: {
			globals: {
				...globals.browser,
				// Vue + unplugin-auto-import
				ref: false
			},
			parser: tsParser,
			parserOptions: {
				project: './tsconfig.json'
			}
		},

		rules: {
			...commonRules,
			// Managed by TypeScript
			'no-undef': 'off'
		}
	},

	{
		files: [
			'*.ts'
		],

		languageOptions: {
			parser: tsParser,
			parserOptions: {
				project: './tsconfig.node.json'
			}
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
			parser: vueParser,
			parserOptions: {
				// See https://eslint.vuejs.org/user-guide/#how-to-use-a-custom-parser
				parser: tsParser,
				project: './tsconfig.json',
				extraFileExtensions: ['.vue']
			}
		},

		rules: {
			...commonRules,
			// Managed by TypeScript
			'no-undef': 'off',
			// See https://github.com/vuejs/language-tools/issues/47
			'no-unused-vars': 'off',
			'@typescript-eslint/no-unused-vars': 'off',
			// Broken in conjunction with tsParser
			'vue/valid-v-for': 'off',
			// These are irrelevant for Vue SFC's, not working properly, or very cumbersome
			'@typescript-eslint/naming-convention': 'off',
			'@typescript-eslint/unbound-method': 'off',
			'@typescript-eslint/no-unnecessary-condition': 'off',
			// TODO: enable some of these little by little
			'@typescript-eslint/no-unsafe-assignment': 'off',
			'@typescript-eslint/explicit-function-return-type': 'off',
			'@typescript-eslint/no-unsafe-member-access': 'off',
			// For this one, maybe allow some cases, like nullable strings?
			'@typescript-eslint/strict-boolean-expressions': 'off',
			'@typescript-eslint/explicit-module-boundary-types': 'off',
			'@typescript-eslint/no-unsafe-call': 'off',
			'@typescript-eslint/no-unsafe-return': 'off',
			'@typescript-eslint/no-unsafe-argument': 'off'
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
