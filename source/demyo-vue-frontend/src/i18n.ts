import { loadReaderLanguageFromLocalStorage } from '@/helpers/reader'
import datetimeFormats from '@/locales/dateTimeFormats.json'
import { apiRoot, defaultLanguage } from '@/myenv'
// By default, we start with partial messages covering all above-the-fold content in all supported languages
// (mainly titles, but also the search widget, snack bar)
import mergedMessages from '@intlify/unplugin-vue-i18n/messages'
import axios from 'axios'
import { createI18n } from 'vue-i18n'

function setHtmlLang(lang: string): void {
	document.documentElement.setAttribute('lang', lang.replace(/_/g, ' '))
}

const loadedLanguages: string[] = []

// Define the variants we support
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const typedDatetimeFormats = datetimeFormats as Record<string, any>
typedDatetimeFormats['fr-BE'] = datetimeFormats.fr

/* The base language is
 * - Loaded from the local storage if possible
 * - Taken from the navigator otherwise
 * - Replaced with the up-to-date reader config as soon as possible
 */
const localStorageLanguage = loadReaderLanguageFromLocalStorage()
if (localStorageLanguage) {
	console.log('Restoring language from local storage to', localStorageLanguage)
}
const selectedLocale = localStorageLanguage ?? defaultLanguage
const simpleLocale = selectedLocale.replace(/[-_].*/, '')
// English would have no fallback but we're sure French will remain a first-class citizen
const fallbackLanguage = simpleLocale === 'en' ? 'fr' : 'en'
const i18n = createI18n({
	// Use the simple locale for the JSON files, which aren't as lenient as the backend
	locale: simpleLocale,
	fallbackLocale: fallbackLanguage,
	messages: mergedMessages,
	datetimeFormats: typedDatetimeFormats,
	legacy: false
})
setHtmlLang(selectedLocale)
console.log(`Initialized i18n with '${selectedLocale}' as default language and '${fallbackLanguage}' as fallback`)

/**
 * Asynchronously gets translations for a given language from the server, and sets them in vue-i18n.
 * @param {string} lang The language to get translations for
 * @return {Promise<void>} An Promise without value (void)
 */
async function loadLanguageFromServer(lang: string): Promise<void> {
	if (loadedLanguages.includes(lang)) {
		console.log(`Language ${lang} was already loaded, it won't be loaded again`)
		return
	}
	const response = await axios.get(`${apiRoot}translations/${lang}`)
	i18n.global.setLocaleMessage(lang, response.data)
	console.log(`Loaded ${Object.keys(response.data as object).length} translations from the server in ${lang}`)
	loadedLanguages.push(lang)
}

// i18n is already initialized and serving the critical messages in the language of choice
// Now, we should load the full translations in the default and fallback languages
// Load the default language, and then the fallback (prioritise the default rather than relying on luck)
void loadLanguageFromServer(defaultLanguage).then(
	async () => loadLanguageFromServer(fallbackLanguage))

/**
 * Switches to a different language, potentially loading translations if needed
 * @param {String} lang the new language
 */
export async function switchLanguage(lang: string): Promise<void> {
	// Java and browsers have a different way of formatting language variants
	lang = lang.replace(/_/g, '-')
	console.log(`Switching language to ${lang}`)
	await loadLanguageFromServer(lang)
	i18n.global.locale.value = lang
	setHtmlLang(lang)
}

export const $t = i18n.global.t

export default i18n
