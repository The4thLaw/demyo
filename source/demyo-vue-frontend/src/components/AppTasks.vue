<template>
	<portal to="appTasks">
		<v-menu offset-y>
			<template v-slot:activator="{ on }">
				<v-btn text icon v-on="on">
					<v-icon>mdi-dots-vertical</v-icon>
				</v-btn>
			</template>
			<v-list>
				<v-list-item @click="">
					<v-list-item-title>Test</v-list-item-title>
				</v-list-item>
			</v-list>
		</v-menu>
	</portal>
</template>

<script>
export default {
	name: 'AppTasks'
}
</script>
