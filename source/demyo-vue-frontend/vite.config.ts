/* eslint-disable @typescript-eslint/naming-convention */
import VueI18nPlugin from '@intlify/unplugin-vue-i18n/vite'
import vue from '@vitejs/plugin-vue'
import autoprefixer from 'autoprefixer'
import * as path from 'path'
import { visualizer } from 'rollup-plugin-visualizer'
import autoImport from 'unplugin-auto-import/vite'
import autoComponents from 'unplugin-vue-components/vite'
import type { ConfigEnv, UserConfig } from 'vite'
import { defineConfig } from 'vite'
import vuetify from 'vite-plugin-vuetify'

export default ({ mode }: ConfigEnv): UserConfig => {
	return defineConfig({
		server: {
			proxy: {
				'/api': { target: 'http://localhost:1607/' },
				'^/images/.*/file/.*': { target: 'http://localhost:1607/' }
			}
		},

		build: {
			modulePreload: mode !== 'development',
			sourcemap: true,

			rollupOptions: {
				output: {
					manualChunks(id) {
						if (id.includes('filepond')) {
							return '@vendor-filepond'
						}

						if (id.includes('prosemirror')) {
							return '@vendor-prosemirror'
						}

						// Note: for some reason, code-splitting other vendors fails due to undefined
						// references and so on, they need to stay here
						if (id.includes('node_modules')) {
							return '@vendor-various'
						}

						if (id.includes('Edit.vue') || id.includes('ImageDetect.vue')
							|| id.includes('ManageImport.vue') || id.includes('ManageExport.vue')) {
							return '@demyo-manage'
						}

						return undefined
					}
				}
			}
		},

		base: '/',

		resolve: {
			alias: {
				'@': path.resolve(__dirname, './src'),
				'vue-i18n-bridge': 'vue-i18n-bridge/dist/vue-i18n-bridge.runtime.esm-bundler.js'
			}
		},

		define: {
			// See https://vue-i18n.intlify.dev/guide/advanced/optimization#jit-compilation
			// Avoids CSP issues with unsafe-eval
			__INTLIFY_JIT_COMPILATION__: true
		},

		plugins: [
			vue(),
			vuetify(),
			autoComponents({
				dirs: ['src/components', 'src/pages']
			}),
			autoImport({
				imports: [
					// Presets
					'vue'
				]
			}),
			VueI18nPlugin({
				include: [path.resolve(__dirname, './src/locales/**')]
			}),
			visualizer()
		],

		css: {
			postcss: {
				plugins: [
					autoprefixer({})
				]
			}
		}
	})
}
