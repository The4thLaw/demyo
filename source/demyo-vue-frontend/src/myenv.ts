function getDataWithDefault(key: string, defaultValue: string): string {
	const dataValue = document.body.dataset[key]
	return dataValue ?? defaultValue
}

// Configure the context root
let bodyContextRoot = getDataWithDefault('contextroot', import.meta.env.VITE_CONTEXT_ROOT_FALLBACK)
if (!bodyContextRoot.endsWith('/')) {
	bodyContextRoot += '/'
}
if (!bodyContextRoot.startsWith('/')) {
	bodyContextRoot = `/${bodyContextRoot}`
}
console.log('Context root is', bodyContextRoot)
export const contextRoot = bodyContextRoot

// Configure the API root
const bodyApiRoot = getDataWithDefault('apiroot', import.meta.env.VITE_API_FALLBACK_ENDPOINT)
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
const bodyVersion = getDataWithDefault('version', 'x.y.z')
const bodyCodename = getDataWithDefault('codename', '[Vue]')
export const demyoVersion = bodyVersion
export const demyoCodename = bodyCodename

// Extract the Content-Security-Policy nonce
export const cspStyleNonce = document.body.dataset.cspstylenonce
