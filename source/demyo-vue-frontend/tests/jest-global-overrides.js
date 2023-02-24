// See https://stackoverflow.com/a/68468204/109813 and https://github.com/jsdom/jsdom/issues/2524#issuecomment-899481592
import { TextDecoder, TextEncoder } from 'util'
global.TextEncoder = TextEncoder
global.TextDecoder = TextDecoder

const error = console.error

// Watch out for console errors
console.error = function (message) {
	error.apply(console, arguments) // keep default behaviour
	throw (message instanceof Error ? message : new Error(message))
}
