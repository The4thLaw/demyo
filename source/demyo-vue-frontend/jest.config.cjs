/* eslint-disable @typescript-eslint/no-var-requires */

// This file follows the guide from
// https://vue-test-utils.vuejs.org/installation/#using-vue-test-utils-with-jest-recommended
module.exports = {
	preset: '@vue/cli-plugin-unit-jest',

	setupFiles: [
		'<rootDir>/tests/jest-global-overrides.js'
	],

	setupFilesAfterEnv: [
		'<rootDir>/tests/jest-vue-setup.js'
	],

	transformIgnorePatterns: [
		// Vuetify uses exports. Apparently only required for tiptap-vuetify. Maybe one day we
		// should disable this and try to use Jest mocks for tiptap if it's indeed the only one

		// Axios 1.x uses ES imports rather than CommonJS
		// See https://stackoverflow.com/a/74079349/109813
		// Might not be needed once we migrate to Jest 29+
		'<rootDir>/node_modules/(?!(vuetify|axios)).*'
	],

	// Coverage
	collectCoverage: true,
	collectCoverageFrom: ['**/*.{js,vue}', '!**/node_modules/**', '!**/dist/**', '!**/target/**'],
	coverageReporters: ['lcov'],
	coverageDirectory: 'target/jest-coverage'
}

// See https://github.com/vuejs/vue-test-utils/issues/193#issuecomment-567555536
require('dotenv').config()
