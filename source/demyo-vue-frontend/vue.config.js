module.exports = {
	'lintOnSave': false,

	devServer: {
		// Route requests to unknown URLs to the backend (can be JSON server or Spring)
		proxy: {
			'/api': { target: 'http://localhost:1607/' },
			// TODO: check if we can refine the mapping to just take the files (something like /images/:id/file/)
			'/images': { target: 'http://localhost:1607/' }
		}
	},

	'pluginOptions': {
		'i18n': {
			'locale': 'en',
			'fallbackLocale': 'en',
			'localeDir': 'locales',
			'enableInSFC': false
		},
		'webpackBundleAnalyzer': {
			'openAnalyzer': false
		}
	},
	'transpileDependencies': [
		'vuetify'
	]
}
