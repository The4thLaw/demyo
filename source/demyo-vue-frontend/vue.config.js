module.exports = {
	lintOnSave: false,

	// See
	// - https://cli.vuejs.org/config/#publicpath
	// - https://stackoverflow.com/a/65924415/109813
	publicPath: './',

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

		if (process.env.NODE_ENV !== 'production') {
			// Don't preload / prefetch. Helps debugging the lazy-loading
			config.plugins.delete('prefetch')
			config.plugins.delete('preload')
		}
	}
}
