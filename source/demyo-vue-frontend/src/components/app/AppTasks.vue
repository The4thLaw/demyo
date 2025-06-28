<template>
	<Teleport to="#teleport-appTasks">
		<v-menu v-model="model">
			<template #activator="{ props }">
				<v-btn variant="text" icon="mdi-dots-vertical" v-bind="props" />
			</template>
			<v-list class="c-AppTasks__list">
				<slot />
			</v-list>
		</v-menu>
	</Teleport>
</template>

<script setup lang="ts">
const model = defineModel<boolean | undefined>()
</script>

<style lang="scss">
.c-AppTasks__list {
	a,
	a:hover {
		text-decoration: none;
	}
}
</style>
