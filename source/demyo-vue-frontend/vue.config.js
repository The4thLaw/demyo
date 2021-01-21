module.exports = {
	lintOnSave: false,

	devServer: {
		// Route requests to unknown URLs to the backend (can be JSON server or Spring)
		proxy: {
			'/api': { target: 'http://localhost:1607/' },
			'^/images/.*/file/.*': { target: 'http://localhost:1607/' }
		}
	},

	pluginOptions: {
		i18n: {
			locale: 'en',
			fallbackLocale: 'en',
			localeDir: 'locales',
			enableInSFC: false
		},
		webpackBundleAnalyzer: {
			openAnalyzer: false,
			defaultSizes: 'parsed'
		}
	},

	transpileDependencies: [
		'vuetify'
	],

	chainWebpack: config => {
		config.module
			.rule('vue')
			.use('vue-loader')
			.loader('vue-loader')
			.tap(options => {
				// modify the options...
				options.whitespace = 'condense'
				return options
			})

		if (process.env.NODE_ENV === 'production') {
			// Use eval-less source maps
			config.merge({ devtool: 'source-map' })
		}

		// Make a chunk for tiptap and related, and one for filepond. They're pretty big
		// Inspired by https://itnext.io/how-to-boost-vue-js-performance-c7df027ff3f5
		// This is commented out because it's mainly useful to benefit from the fact that some
		// chunks may be updated less often and thus don't have to be re-downloaded
		// But Demyo isn't updated frequently enough for this to really make sense
		// True lazy-load would be more interesting, see
		// https://forum.vuejs.org/t/lazy-load-3rd-party-library-like-leaflet/7122/2
		// and try to combine it with webpackChunkName and
		// https://vueschool.io/articles/vuejs-tutorials/lazy-loading-individual-vue-components-and-prefetching/
		// Maybe use webpackMode: "lazy" too ?
		/*
		config.optimization.splitChunks({
			chunks: 'all',
			maxInitialRequests: Infinity,
			minSize: 0,
			cacheGroups: {
				vendor: {
					test: /[\\/]node_modules[\\/]/,
					name(module) {
						if (module.context.match(/tiptap|prosemirror/)) {
							return 'vendor-tiptap'
						}
						if (module.context.match(/filepond/)) {
							return 'vendor-filepond'
						}
						return 'vendor-common'
					}
				}
			}
		})
		*/

		if (process.env.NODE_ENV !== 'production') {
			// Don't preload / prefetch. Helps debugging the lazy-loading
			config.plugins.delete('prefetch')
			config.plugins.delete('preload')
		}
	}
}
