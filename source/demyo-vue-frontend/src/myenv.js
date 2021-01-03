// Configure the context root
let bodyContextRoot = document.body.dataset.contextRoot
if (bodyContextRoot === undefined) {
	bodyContextRoot = process.env.VUE_APP_CONTEXT_ROOT_FALLBACK
}
if (!bodyContextRoot.endsWith('/')) {
	bodyContextRoot += '/'
}
console.log('Context root is', bodyContextRoot)
export const contextRoot = bodyContextRoot

// Configure the API root
let bodyApiRoot = document.body.dataset.apiroot
if (bodyApiRoot === undefined) {
	bodyApiRoot = process.env.VUE_APP_API_FALLBACK_ENDPOINT
}
console.log('API root is', bodyApiRoot)
export const apiRoot = bodyApiRoot

// Configure the default and fallback languages
let navigatorLanguage = navigator.language.replace(/[-_].*/, '')
if (navigatorLanguage !== 'en' && navigatorLanguage !== 'fr') {
	// We only support those two at the moment. By the time we support more, this code will have been removed
	navigatorLanguage = 'en'
}
export const defaultLanguage = navigatorLanguage

// Extract the version and codename
let bodyVersion = document.body.dataset.version
if (bodyVersion === undefined) {
	bodyVersion = 'x.y.z'
}
let bodyCodename = document.body.dataset.codename
if (bodyCodename === undefined) {
	bodyCodename = '[Vue]'
}
export const demyoVersion = bodyVersion
export const demyoCodename = bodyCodename
