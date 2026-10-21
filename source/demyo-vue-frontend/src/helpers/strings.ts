export function capitalize(str: string | undefined): string | undefined {
	if (!str) {
		return str
	}

	const firstCodePoint = str.codePointAt(0)
	if (!firstCodePoint) {
		return str
	}
	const index = firstCodePoint > 0xFFFF ? 2 : 1

	return String.fromCodePoint(firstCodePoint).toUpperCase() + str.slice(index)
}
