<template>
	<!-- TODO: when adding actual content to this component, it should remove the overlay on load
	to provide a user-accessible reset in case of issue -->
	<div class="home">
		<img alt="Vue logo" src="../assets/logo.png">
	</div>
</template>

<script>

export default {
	name: 'Home',

	created() {
		this.$store.dispatch('ui/disableSearch')
	}
}
</script>
