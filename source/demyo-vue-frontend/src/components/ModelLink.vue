<template>
	<router-link :to="{ name: view, params: { id: model.id }}" class="c-ModelLink">
		{{ model.identifyingName }}
	</router-link>
</template>

<script>
export default {
	name: 'ModelLink',

	props: {
		model: {
			type: null,
			required: true
		},

		view: {
			type: String,
			required: true
		}
	}
}
</script>

<style lang="less">
a.c-ModelLink:not(:hover) {
	color: inherit;
}
</style>
