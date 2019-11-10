module.exports = {
	'lintOnSave': false,

	devServer: {
		// Route requests to unknown URLs to the backend (can be JSON server or Spring)
		proxy: {
			'/api': { target: 'http://localhost:3000/' }
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
