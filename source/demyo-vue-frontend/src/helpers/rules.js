export function mandatory(comp) {
	return v => !!v || comp.$t('validation.mandatory')
}
