<template>
	<!-- TODO: button + dialog + events -->
	<AOLWizard :term="term" />
</template>

<script setup lang="ts">
defineProps<{
	term: string
}>()
</script>
