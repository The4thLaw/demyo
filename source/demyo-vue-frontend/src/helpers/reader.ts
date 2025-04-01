const LOCAL_STORAGE_READER_KEY = 'currentReader'

/**
 * Loads the reader from the local storage, if possible.
 * @return The Reader, or undefined if it couldn't be loaded.
 */
export function loadReaderFromLocalStorage(): Reader | undefined {
	const readerStr = localStorage.getItem(LOCAL_STORAGE_READER_KEY)
	if (readerStr) {
		return JSON.parse(readerStr)
	}
	return undefined
}

/**
 * Loads the last selected reader's language from the local storage, if possible.
 * @return The language, or null if it couldn't be loaded.
 */
export function loadReaderLanguageFromLocalStorage(): string | undefined {
	const reader = loadReaderFromLocalStorage()
	return reader?.configuration.language
}

/**
 * Saves a reader to the local storage.
 * @param reader The reader to save
 */
export function saveReaderToLocalStorage(reader: Reader) {
	localStorage.setItem(LOCAL_STORAGE_READER_KEY, JSON.stringify(reader))
}
