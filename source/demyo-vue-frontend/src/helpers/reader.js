const LOCAL_STORAGE_READER_KEY = 'currentReader'

/**
 * Loads the reader from the local storage, if possible.
 * @return {any} The Reader, or null if it couldn't be loaded.
 */
export function loadReaderFromLocalStorage() {
	const readerStr = localStorage.getItem(LOCAL_STORAGE_READER_KEY)
	if (readerStr) {
		return JSON.parse(readerStr)
	}
	return null
}

/**
 * Loads the last selected reader's language from the local storage, if possible.
 * @return {String} The language, or null if it couldn't be loaded.
 */
export function loadReaderLanguageFromLocalStorage() {
	const reader = loadReaderFromLocalStorage()
	return reader?.configuration?.language
}

/**
 * Saves a reader to the local storage.
 * @param {any} reader The reader to save
 */
export function saveReaderToLocalStorage(reader) {
	localStorage.setItem(LOCAL_STORAGE_READER_KEY, JSON.stringify(reader))
}
