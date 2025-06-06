export function useRichTextTemplate(userValue: Ref<string | undefined>): Ref<string | undefined> {
	return computed(() => {
		console.log(userValue.value)
		if (!userValue.value) {
			return userValue.value
		}
		return userValue.value
			// Tags
			.replace(/\(tag:(\d+)\)/g, '<DeferredTagLink tag-id="$1"/>')
	})
}
