export function useRichTextTemplate(userValue: Ref<string | undefined>): Ref<string | undefined> {
	return computed(() => {
		console.log(userValue.value)
		if (!userValue.value) {
			return userValue.value
		}
		return userValue.value
			// Authors
			.replace(/(\[([^\]]*)\])?\((author):(\d+)\)/g,
				'<DeferredModelLink type="$3" model-id="$4" label="$2"/>')
			// Tags
			.replace(/(\[([^\]]*)\])?\(tag:(\d+)\)/g, '<DeferredTagLink tag-id="$3" label="$2"/>')
	})
}
