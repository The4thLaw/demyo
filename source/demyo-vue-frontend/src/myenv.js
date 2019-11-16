// Configure the API root
var bodyApiRoot = document.body.dataset.apiroot
if (bodyApiRoot === undefined) {
	bodyApiRoot = process.env.VUE_APP_API_FALLBACK_ENDPOINT
}
console.log('API root is', bodyApiRoot)
export const apiRoot = bodyApiRoot

// Configure the default and fallback languages
var navigatorLanguage = navigator.language.replace(/-.*/, '')
if (navigatorLanguage !== 'en' && navigatorLanguage !== 'fr') {
	// We only support those two at the moment. By the time we support more, this code will have been removed
	navigatorLanguage = 'en'
}
export const defaultLanguage = navigatorLanguage
// English would have no fallback but we're sure French will remain a first-class citizen
export const fallbackLanguage = navigatorLanguage === 'en' ? 'fr' : 'en'
