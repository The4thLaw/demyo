const error = console.error

// Watch out for console errors
console.error = function (message) {
	error.apply(console, arguments) // keep default behaviour
	throw (message instanceof Error ? message : new Error(message))
}
