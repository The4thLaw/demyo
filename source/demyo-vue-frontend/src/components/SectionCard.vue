<template>
	<v-card :loading="loading ? 'primary' : false">
		<div class="pa-6">
			<slot />
		</div>
	</v-card>
</template>

<script>
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
