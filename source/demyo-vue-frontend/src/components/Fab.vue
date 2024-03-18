<template>
	<v-btn class="c-Fab" color="secondary" v-bind="$attrs" />
</template>

<script>
export default {
	name: 'FabButton'
}
</script>

<style type="css">
.c-Fab {
	z-index: 1;
	position: fixed;
	bottom: 16px;
	right: 16px;
}
</style>
