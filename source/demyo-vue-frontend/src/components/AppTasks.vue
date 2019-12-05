<template>
	<portal to="appTasks">
		<v-menu offset-y>
			<template v-slot:activator="{ on }">
				<v-btn text icon v-on="on">
					<v-icon>mdi-dots-vertical</v-icon>
				</v-btn>
			</template>
			<v-list class="c-AppTasks__list">
				<slot />
			</v-list>
		</v-menu>
	</portal>
</template>

<script>
export default {
	name: 'AppTasks'
}
</script>

<style lang="less">
.c-AppTasks__list {
	a,
	a:hover {
		text-decoration: none;
	}
}
</style>
