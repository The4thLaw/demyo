export function mandatory(comp) {
	return v => !!v || comp.$t('validation.mandatory')
}

export function integer(comp) {
	// Note that this rule only works partially because browsers will return an empty value for
	// number fields. But that's the best we can do while keeping up/down arrows.
	return v => {
		if (!v) {
			return true
		}

		return !!String(v).match(/^[0-9]*$/) || comp.$t('validation.integer')
	}
}
