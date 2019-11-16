import Vue from 'vue'
import VueI18n from 'vue-i18n'
import axios from 'axios'
import { apiRoot, defaultLanguage, fallbackLanguage } from '@/myenv'

Vue.use(VueI18n)

// By default, we start with partial messages covering all above-the-fold content in all supported languages
// (mainly titles)
// TODO: add more messages to those files (list them all)
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

// TODO: eventually, load the Reader preferences to get the right language, and load that one and
// the fallback. Stop relying on the navigator language.

const i18n = new VueI18n({
	locale: defaultLanguage,
	fallbackLocale: fallbackLanguage,
	messages: loadLocaleMessages()
})
console.log(`Initialized i18n with '${defaultLanguage}' as default language and '${fallbackLanguage}' as fallback`)

/**
 * Asynchronously gets translations for a given language from the server, and sets them in vue-i18n.
 * @param {string} lang The language to get translations for
 * @return {Promise} A Promise that always resolves to true
 */
async function loadLanguageFromServer(lang) {
	let response = await axios.get(apiRoot + 'translations/' + lang)
	i18n.setLocaleMessage(lang, response.data)
	console.log(`Loaded ${Object.keys(response.data).length} translations from the server in ${lang}`)
	return true
}

// i18n is already initialized and serving the critical messages in the language of choice
// Now, we should load the full translations in the default and fallback languages
// Load the default language, and then the fallback (prioritise the default rather than relying on luck)
loadLanguageFromServer(defaultLanguage).then(
	() => loadLanguageFromServer(fallbackLanguage))

export default i18n
