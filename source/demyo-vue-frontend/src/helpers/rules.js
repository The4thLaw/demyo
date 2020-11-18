// TODO: import i18n
// import i18n, { switchLanguage } from '@/i18n'
// then i18n.t ...
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

export function email(comp) {
	// Not really the right regex but it's sufficient
	return regexMatch(comp, /^[^@]+@[^@]+$/, 'validation.email')
}

export function isbn(comp) {
	return regexMatch(comp, /^[0-9-]{10,}X?( ?\/ ?[0-9]+)?$/i, 'validation.isbn')
}

export function integer(comp) {
	// Note that this rule only works partially because browsers will return an empty value for
	// number fields. But that's the best we can do while keeping up/down arrows.
	return regexMatch(comp, /^[0-9]*$/, 'validation.integer')
}

export function number(comp) {
	// Note that this rule only works partially because browsers will return an empty value for
	// number fields. But that's the best we can do while keeping up/down arrows.
	return regexMatch(comp, /^([0-9]*|[0-9]+\.[0-9]+)$/, 'validation.number')
}

export function phone(comp) {
	return regexMatch(comp, /^\+?[0-9() /-]+$/, 'validation.phone')
}

export function url(comp) {
	return regexMatch(comp, /^https?:\/\/.*$/, 'validation.url')
}
