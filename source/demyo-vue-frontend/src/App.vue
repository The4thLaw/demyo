<template>
	<v-app id="demyo">
		<v-navigation-drawer v-model="mainMenu" app>
			<v-list>
				<v-list-item href="/">
					<v-list-item-icon>
						<v-icon>mdi-home</v-icon>
					</v-list-item-icon>
					<v-list-item-content>
						<v-list-item-title>{{ $t('menu.main.home') }}</v-list-item-title>
					</v-list-item-content>
				</v-list-item>

				<v-list-group v-for="section in menuItems" :key="section.title" v-model="section.active">
					<template v-slot:activator>
						<v-list-item-content>
							<v-list-item-title v-text="$t(section.title)" />
						</v-list-item-content>
					</template>

					<v-list-item v-for="item in section.subItems" :key="item.title" :href="item.url">
						<v-list-item-icon>
							<v-icon v-text="item.icon" />
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title v-text="item.title" />
						</v-list-item-content>
					</v-list-item>
				</v-list-group>
			</v-list>
		</v-navigation-drawer>
		<v-app-bar color="primary" dark app>
			
			<v-app-bar-nav-icon @click.stop="mainMenu = !mainMenu" />
			<v-toolbar-title>TODO: title</v-toolbar-title>
			<v-spacer />
			TODO: search
		</v-app-bar>

		<v-content>
			<HelloWorld />
			<v-footer color="secondary" inset dark>
				<v-col>
					TODO: codename
				</v-col>
			</v-footer>
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
			mainMenu: false,

			menuItems: [
				{
					title: 'menu.section.view',
					active: true,
					subItems: [
						{
							title: 'menu.series.browse',
							icon: 'mdi-layers-triple',
							url: '/series'
						},
						{
							title: 'menu.albums.browse',
							icon: 'mdi-book-open-variant',
							url: '/albums'
						},
						{
							title: 'menu.authors.browse',
							icon: 'mdi-account',
							url: '/authors'
						}
					]
				},
				{
					title: 'menu.section.edit',
					subItems: [
						{
							title: 'menu.albums.add',
							icon: 'mdi-book-open-variant',
							url: '/albums/add'
						},
						{
							title: 'menu.authors.add',
							icon: 'mdi-account-plus',
							url: '/authors/add'
						},
						{
							title: 'menu.series.add',
							icon: 'mdi-layers-plus',
							url: '/series/add'
						}
					]
				}
			]
		}
	}
}
</script>

<style lang="less">
html[lang], #demyo {
	font-family: -apple-system, BlinkMacSystemFont, Roboto, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
}

#demyo .v-list-item__title {
	font-size: 14px;
}
</style>
