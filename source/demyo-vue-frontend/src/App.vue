<template>
	<v-app id="demyo">
		<v-navigation-drawer v-model="mainMenu" app temporary>
			<v-list class="c-App__menuList">
				<v-list-group>
					<template v-slot:activator>
						<v-list-item-icon>
							<LetterIcon letter="T" color="#0000ff" />
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title>TODO: reader</v-list-item-title>
						</v-list-item-content>
					</template>

					<v-list-item href="/TODO">
						<v-list-item-icon>
							<v-icon>mdi-heart</v-icon>
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title>{{ $t('menu.reader.faves') }}</v-list-item-title>
						</v-list-item-content>
					</v-list-item>
					<v-list-item href="/TODO">
						<v-list-item-icon>
							<v-icon>mdi-bookmark-multiple</v-icon>
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title>{{ $t('menu.reader.readingList') }}</v-list-item-title>
						</v-list-item-content>
					</v-list-item>
					<v-list-item href="/readers">
						<v-list-item-icon>
							<v-icon>mdi-account-convert</v-icon>
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title>{{ $t('menu.reader.switch') }}</v-list-item-title>
						</v-list-item-content>
					</v-list-item>
				</v-list-group>

				<v-divider />

				<v-list-item to="/">
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

					<v-list-item v-for="item in section.subItems" :key="item.title" :to="item.url">
						<v-list-item-icon>
							<v-icon v-text="item.icon" />
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title v-text="$t(item.title)" />
						</v-list-item-content>
					</v-list-item>
				</v-list-group>
			</v-list>
		</v-navigation-drawer>
		<v-app-bar color="primary" dark app>
			<v-app-bar-nav-icon @click.stop="mainMenu = !mainMenu" />
			<v-toolbar-title>{{ pageTitle }}</v-toolbar-title>
			<v-spacer />
			<template v-if="!suppressSearch">
				TODO: search
			</template>
			<portal-target name="appTasks" />
		</v-app-bar>

		<v-content>
			<v-container fluid>
				<v-overlay absolute z-index="4" :value="globalOverlay" class="c-App__overlay">
					<v-progress-circular :indeterminate="true" color="primary" size="96" width="8" />
					<span class="c-App__overlayText">{{ $t('core.loading') }}</span>
				</v-overlay>
				<router-view />
				<AppSnackbar :shown="displaySnackbar" :message="snackbarMessage" @close="closeSnackbar" />
			</v-container>
			<!-- TODO: Remove the component -->
			<!--<HelloWorld />-->
			<!-- TODO: flush this to the end of the page -->
			<v-footer color="secondary" inset dark>
				<v-col>
					TODO: codename
				</v-col>
			</v-footer>
		</v-content>
	</v-app>
</template>

<script>
import { mapState } from 'vuex'
import AppSnackbar from '@/components/AppSnackbar'
import HelloWorld from './components/HelloWorld'
import LetterIcon from './components/LetterIcon'

export default {
	name: 'App',

	components: {
		AppSnackbar,
		HelloWorld,
		LetterIcon
	},

	metaInfo() {
		let self = this

		return {
			title: this.$t('title.home'),
			titleTemplate: '%s — Demyo',
			changed(newInfo, addedTags, removedTags) {
				self.pageTitle = newInfo.titleChunk
			}
		}
	},

	data() {
		return {
			pageTitle: 'Demyo',

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
	},

	computed: {
		...mapState({
			suppressSearch: state => state.ui.suppressSearch,
			globalOverlay: state => state.ui.globalOverlay,
			displaySnackbar: state => state.ui.displaySnackbar,
			snackbarMessage: state => state.ui.snackbarMessages[0]
		})
	},

	methods: {
		closeSnackbar() {
			this.$store.dispatch('ui/closeSnackbar')
		}
	}
}
</script>

<style lang="less">
html[lang], #demyo {
	font-family: -apple-system, BlinkMacSystemFont, Roboto, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
	background: #eee;
}

#demyo .c-App__overlay .v-overlay__content {
	position: absolute;
	top: 3em;
}

.c-App__overlayText {
	font-size: 1.5rem;
	padding-left: 1em;
}

#demyo .v-list-item__title {
	font-size: 14px;
}

.v-toolbar .v-input {
	caret-color: inherit !important;
}

.v-application a {
	text-decoration: none;

	&:hover {
		text-decoration: underline;
	}
}

.c-App__menuList {
	a,
	a:hover {
		text-decoration: none;
	}
}
</style>
