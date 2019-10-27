<template>
	<v-app>
		<v-navigation-drawer v-model="mainMenu" app>
			TODO: menu
		</v-navigation-drawer>
		<v-app-bar color="primary" dark app>
			
			<v-app-bar-nav-icon @click.stop="mainMenu = !mainMenu" />
			<v-toolbar-title>TODO: title</v-toolbar-title>
			<v-spacer />
			TODO: search
		</v-app-bar>

		<v-content>
			<HelloWorld />
		</v-content>
	</v-app>
</template>

<script>
import HelloWorld from './components/HelloWorld'

export default {
	name: 'App',

	components: {
		HelloWorld
	},

	data() {
		return {
			mainMenu: false
		}
	}
}
</script>
