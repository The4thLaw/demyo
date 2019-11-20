<template>
	<v-card :loading="loading ? 'primary' : false">
		<div class="pa-6">
			<slot />
		</div>
	</v-card>
</template>

<script>
/**
 * A card used to section content.
 * Sports standard padding and loading style.
 */
export default {
	name: 'SectionCard',

	props: {
		loading: {
			type: Boolean,
			required: false,
			default: false
		}
	}
}
</script>
