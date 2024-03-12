import vue from "@vitejs/plugin-vue2";
import autoprefixer from 'autoprefixer';
import * as path from 'path';
import { visualizer } from "rollup-plugin-visualizer";
import { VuetifyResolver } from 'unplugin-vue-components/resolvers';
import Components from 'unplugin-vue-components/vite';
import { defineConfig } from 'vite';

export default (({ mode }) => {
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
						if (id.includes('tiptap')) {
							return '@vendor-tiptap'
						}
						// Note: for some reason, code-splitting other vendors fails due to undefined references and so on, they need to stay here
						if (id.includes('node_modules')) {
							return '@vendor-various'
						}
						if (id.includes('Edit.vue') || id.includes('ImageDetect.vue')
							|| id.includes('Import.vue') || id.includes('Export.vue')) {
							return '@demyo-manage'
						}
					}
				}
			}

			/*rollupOptions: {
				output: {
					manualChunks(id) {
						if (id.includes('filepond')) {
							return '@vendor-filepond'
						}
						if (id.includes('prosemirror')) {
							return '@vendor-prosemirror'
						}
						if (id.includes('tiptap')) {
							return '@vendor-tiptap'
						}
						if (id.includes('node_modules')) {
							return '@vendor-various'
						}
						if (id.includes('Edit.vue') || id.includes('ImageDetect.vue')
							|| id.includes('Import.vue') || id.includes('Export.vue')) {
							return '@demyo-manage'
						}
					}
				}
			}*/
		},

		base: '/',

		resolve: {
			alias: {
				'@': path.resolve(__dirname, "./src"),
				'vue-i18n-bridge': 'vue-i18n-bridge/dist/vue-i18n-bridge.runtime.esm-bundler.js'
			}
		},

		plugins: [
			vue(),
			// Vuetify + Vue 2
			Components({
				resolvers: [
					VuetifyResolver(),
				],
			}),
			visualizer()
		],

		css: {
			postcss: {
				plugins: [
					autoprefixer({})
				],
			}
		}
	})
})

