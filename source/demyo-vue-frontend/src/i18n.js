import Vue from 'vue'
import VueI18n from 'vue-i18n'
import axios from 'axios'
import { apiRoot, defaultLanguage } from '@/myenv'
import { loadReaderLanguageFromLocalStorage } from '@/helpers/reader'

Vue.use(VueI18n)

function setHtmlLang(lang) {
	document.documentElement.setAttribute('lang', lang.replace(/_/g, ' '))
}

const loadedLanguages = []

const dateTimeFormats = {
	en: {
		numeric: {
			year: 'numeric', month: 'numeric', day: 'numeric'
		},
		short: {
			year: 'numeric', month: 'short', day: 'numeric'
		},
		long: {
			year: 'numeric', month: 'long', day: 'numeric'
		}
	},

	fr: {
		numeric: {
			year: 'numeric', month: 'numeric', day: 'numeric'
		},
		short: {
			year: 'numeric', month: 'short', day: 'numeric'
		},
		long: {
			year: 'numeric', month: 'long', day: 'numeric'
		}
	}
}

// Define the variants we support
dateTimeFormats['fr-BE'] = dateTimeFormats.fr

// By default, we start with partial messages covering all above-the-fold content in all supported languages
// (mainly titles, but also the search widget)
function loadLocaleMessages() {
	const locales = require.context('./locales', true, /[A-Za-z0-9-_,\s]+\.json$/i)
	const messages = {}
	locales.keys().forEach(key => {
		const matched = key.match(/([A-Za-z0-9-_]+)\./i)
		if (matched && matched.length > 1) {
			const locale = matched[1]
			messages[locale] = locales(key)
		}
	})
	return messages
}

/* The base language is
 * - Loaded from the local storage if possible
 * - Taken from the navigator otherwise
 * - Replaced with the up-to-date reader config as soon as possible
 */
const localStorageLanguage = loadReaderLanguageFromLocalStorage()
if (localStorageLanguage) {
	console.log('Restoring language from local storage to', localStorageLanguage)
}
const selectedLocale = localStorageLanguage || defaultLanguage
// English would have no fallback but we're sure French will remain a first-class citizen
const fallbackLanguage = selectedLocale.replace(/[-_].*/, '') === 'en' ? 'fr' : 'en'
const i18n = new VueI18n({
	locale: selectedLocale,
	fallbackLocale: fallbackLanguage,
	messages: loadLocaleMessages(),
	dateTimeFormats
})
setHtmlLang(selectedLocale)
console.log(`Initialized i18n with '${selectedLocale}' as default language and '${fallbackLanguage}' as fallback`)

/**
 * Asynchronously gets translations for a given language from the server, and sets them in vue-i18n.
 * @param {string} lang The language to get translations for
 * @return {Promise<void>} An Promise without value (void)
 */
async function loadLanguageFromServer(lang) {
	if (loadedLanguages.includes(lang)) {
		console.log(`Language ${lang} was already loaded, it won't be loaded again`)
		return
	}
	const response = await axios.get(apiRoot + 'translations/' + lang)
	i18n.setLocaleMessage(lang, response.data)
	console.log(`Loaded ${Object.keys(response.data).length} translations from the server in ${lang}`)
	loadedLanguages.push(lang)
}

// i18n is already initialized and serving the critical messages in the language of choice
// Now, we should load the full translations in the default and fallback languages
// Load the default language, and then the fallback (prioritise the default rather than relying on luck)
loadLanguageFromServer(defaultLanguage).then(
	() => loadLanguageFromServer(fallbackLanguage))

/**
 * Switches to a different language, potentially loading translations if needed
 * @param {String} lang the new language
 */
export async function switchLanguage(lang) {
	// Java and browsers have a different way of formatting language variants
	lang = lang.replace(/_/g, '-')
	console.log(`Switching language to ${lang}`)
	await loadLanguageFromServer(lang)
	i18n.locale = lang
	setHtmlLang(lang)
}

export default i18n
