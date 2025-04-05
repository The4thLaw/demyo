/* eslint-disable @typescript-eslint/no-explicit-any */
import { $t } from '@/i18n'

type ValidationFunction = (v: any) => string | true

export function mandatory(): ValidationFunction {
	return (v: any) => !!v || $t('validation.mandatory')
}

function regexMatch(regex: RegExp, key: string): ValidationFunction {
	return (v: any) => {
		if (!v) {
			return true
		}

		return !!RegExp(regex).exec(String(v)) || $t(key)
	}
}

export function email(): ValidationFunction {
	// Not really the right regex but it's sufficient
	return regexMatch(/^[^@]+@[^@]+$/, 'validation.email')
}

export function isbn(): ValidationFunction {
	return regexMatch(/^[0-9-]{10,}X?( ?\/ ?\d+)?$/i, 'validation.isbn')
}

export function integer(): ValidationFunction {
	// Note that this rule only works partially because browsers will return an empty value for
	// number fields. But that's the best we can do while keeping up/down arrows.
	return regexMatch(/^\d*$/, 'validation.integer')
}

export function number(): ValidationFunction {
	// Note that this rule only works partially because browsers will return an empty value for
	// number fields. But that's the best we can do while keeping up/down arrows.
	return regexMatch(/^(\d*|\d+\.\d+)$/, 'validation.number')
}

export function phone(): ValidationFunction {
	return regexMatch(/^\+?[0-9() /.-]+$/, 'validation.phone')
}

export function url(): ValidationFunction {
	return regexMatch(/^https?:\/\/.*$/, 'validation.url')
}
