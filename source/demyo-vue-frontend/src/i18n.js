import Vue from 'vue'
import VueI18n from 'vue-i18n'
import axios from 'axios'
import { apiRoot, defaultLanguage, fallbackLanguage } from '@/myenv'

Vue.use(VueI18n)

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

// The default lang fallback languages are only guessed from the client configuration.
// They will be overridden by the reader service as soon as a reader is available

const i18n = new VueI18n({
	locale: defaultLanguage,
	fallbackLocale: fallbackLanguage,
	messages: loadLocaleMessages(),
	dateTimeFormats
})
console.log(`Initialized i18n with '${defaultLanguage}' as default language and '${fallbackLanguage}' as fallback`)

/**
 * Asynchronously gets translations for a given language from the server, and sets them in vue-i18n.
 * @param {string} lang The language to get translations for
 * @return {Promise} A Promise that always resolves to true
 */
async function loadLanguageFromServer(lang) {
	if (loadedLanguages.includes(lang)) {
		console.log(`Language ${lang} was already loaded, it won't be loaded again`)
		return true
	}
	const response = await axios.get(apiRoot + 'translations/' + lang)
	i18n.setLocaleMessage(lang, response.data)
	console.log(`Loaded ${Object.keys(response.data).length} translations from the server in ${lang}`)
	loadedLanguages.push(lang)
	return true
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
	lang = lang.replace(/_/, '-')
	console.log(`Switching language to ${lang}`)
	await loadLanguageFromServer(lang)
	i18n.locale = lang
}

export default i18n
