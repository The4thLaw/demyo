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
	}
}
