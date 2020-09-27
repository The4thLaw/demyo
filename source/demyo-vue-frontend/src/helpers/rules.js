export function mandatory(comp) {
	return v => !!v || comp.$t('validation.mandatory')
}

function regexMatch(comp, regex, key) {
	return v => {
		if (!v) {
			return true
		}

		return !!String(v).match(regex) || comp.$t(key)
	}
}

export function integer(comp) {
	// Note that this rule only works partially because browsers will return an empty value for
	// number fields. But that's the best we can do while keeping up/down arrows.
	return regexMatch(comp, /^[0-9]*$/, 'validation.integer')
}

export function url(comp) {
	return regexMatch(comp, /^https?:\/\/.*$/, 'validation.url')
}
