import { $t } from '@/i18n'

export function mandatory() {
	return v => !!v || $t('validation.mandatory')
}

function regexMatch(regex, key) {
	return v => {
		if (!v) {
			return true
		}

		return !!String(v).match(regex) || $t(key)
	}
}

export function email() {
	// Not really the right regex but it's sufficient
	return regexMatch(/^[^@]+@[^@]+$/, 'validation.email')
}

export function isbn() {
	return regexMatch(/^[0-9-]{10,}X?( ?\/ ?[0-9]+)?$/i, 'validation.isbn')
}

export function integer() {
	// Note that this rule only works partially because browsers will return an empty value for
	// number fields. But that's the best we can do while keeping up/down arrows.
	return regexMatch(/^[0-9]*$/, 'validation.integer')
}

export function number() {
	// Note that this rule only works partially because browsers will return an empty value for
	// number fields. But that's the best we can do while keeping up/down arrows.
	return regexMatch(/^([0-9]*|[0-9]+\.[0-9]+)$/, 'validation.number')
}

export function phone() {
	return regexMatch(/^\+?[0-9() /.-]+$/, 'validation.phone')
}

export function url() {
	return regexMatch(/^https?:\/\/.*$/, 'validation.url')
}
