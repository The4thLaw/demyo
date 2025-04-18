import vue from '@vitejs/plugin-vue'
import * as path from 'path'
import autoImport from 'unplugin-auto-import/vite'
import autoComponents from 'unplugin-vue-components/vite'
import { defineConfig } from 'vite'
import vuetify from 'vite-plugin-vuetify'

export default defineConfig({
	resolve: {
		alias: {
			'@': path.resolve(__dirname, './src')
			// 'vue-i18n-bridge': 'vue-i18n-bridge/dist/vue-i18n-bridge.runtime.esm-bundler.js'
		}
	},

	plugins: [
		vue(),
		vuetify(),
		autoComponents(),
		autoImport({
			imports: [
				// Presets
				'vue'
			]
		})
	],

	test: {
		globals: true,
		environment: 'jsdom',
		coverage: {
			reporter: ['lcov']
		},
		setupFiles: [
			'tests/setup-plugins.ts',
			'tests/setup-mocks.ts'
		],
		server: {
			deps: {
				inline: ['vuetify'],
				optimizer: {
					web: {
						include: ['vuetify']
					}
				}
			}
		}
	}
})
