<template>
	<v-app id="demyo">
		<v-navigation-drawer v-model="mainMenu" app temporary width="20em">
			<v-list class="c-App__menuList">
				<v-list-group v-if="readerLoaded">
					<template v-slot:activator>
						<v-list-item-icon>
							<LetterIcon :letter="currentReader.name[0]" :color="currentReader.colour" />
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title>{{ currentReader.name }}</v-list-item-title>
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
							<v-icon>mdi-library</v-icon>
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
			<portal-target name="appBarAddons" />
			<portal-target name="appTasks" />
		</v-app-bar>

		<v-content>
			<v-container fluid>
				<ReaderSelection
					v-if="requireReaderSelection || promptReaderSelection" :require-selection="requireReaderSelection"
				/>
				<v-overlay absolute z-index="4" :value="globalOverlay" class="c-App__overlay">
					<v-progress-circular :indeterminate="true" color="primary" size="96" width="8" />
					<span class="c-App__overlayText">{{ $t('core.loading') }}</span>
				</v-overlay>
				<router-view />
				<AppSnackbar :shown="displaySnackbar" :message="snackbarMessage" @close="closeSnackbar" />
			</v-container>
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
import LetterIcon from '@/components/LetterIcon'
import ReaderSelection from '@/components/ReaderSelection'

export default {
	name: 'DemyoApp',

	components: {
		AppSnackbar,
		LetterIcon,
		ReaderSelection
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

			promptReaderSelection: false,

			menuItems: [
				{
					title: 'menu.section.view',
					active: true,
					subItems: [
						{
							title: 'menu.series.browse',
							icon: 'mdi-animation',
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
						},
						{
							title: 'menu.derivatives.browse',
							icon: 'mdi-image-frame',
							url: '/derivatives'
						},
						{
							title: 'menu.derivatives.browse.stickers',
							icon: 'mdi-note-outline',
							url: '/derivatives/stickers'
						},
						{
							title: 'menu.tags.browse',
							icon: 'mdi-tag',
							url: '/tags'
						},
						{
							title: 'menu.publishers.browse',
							icon: 'mdi-office-building',
							// Collection could be folder-multiple or picture-in-picture-bottom-right if we keep Demyo 2.0/2.1
							url: '/publishers'
						},
						{
							title: 'menu.derivative_sources.browse',
							icon: 'mdi-tooltip-account',
							url: '/derivativeSources'
						},
						{
							title: 'menu.derivative_types.browse',
							icon: 'mdi-brush',
							url: '/derivativeTypes'
						},
						{
							title: 'menu.bindings.browse',
							icon: 'mdi-notebook',
							url: '/bindings'
						},
						{
							title: 'menu.images.browse',
							icon: 'mdi-camera',
							url: '/images'
						}
					]
				},
				{
					title: 'menu.section.edit',
					subItems: [
						{
							title: 'menu.albums.add',
							icon: 'mdi-book-open-variant dem-overlay-add',
							url: '/albums/new'
						},
						{
							title: 'menu.authors.add',
							icon: 'mdi-account dem-overlay-add',
							url: '/authors/new'
						},
						{
							title: 'menu.series.add',
							icon: 'mdi-animation dem-overlay-add',
							url: '/series/new'
						}
					]
				}
			]
		}
	},

	computed: {
		...mapState({
			readerLoaded: state => state.reader.readerLoaded,
			requireReaderSelection: state => state.reader.requireReaderSelection,
			currentReader: state => state.reader.currentReader,

			suppressSearch: state => state.ui.suppressSearch,
			globalOverlay: state => state.ui.globalOverlay,
			displaySnackbar: state => state.ui.displaySnackbar,
			snackbarMessage: state => state.ui.snackbarMessages[0],
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
:root {
	// TODO[dark]: alternate colors
	--dem-text: rgba(0, 0, 0, 0.87);
	--dem-primary-contrast: #fff;
	--dem-bg-contrast: #ddd;
	--dem-base-border: #ccc;
}

html[lang],
#demyo {
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

	&:not(.v-btn):hover {
		text-decoration: underline;
	}
}

.v-application .c-App__menuList,
.v-application .c-AppTasks__list {
	a,
	a:hover {
		text-decoration: none;
	}
}

.v-application .v-alert--dense {
	// Super dense alerts
	font-size: 90%;
	padding-top: 4px;
	padding-bottom: 4px;

	.v-alert__icon.v-icon {
		font-size: 20px;
		margin-right: 8px;
	}
}

.dem-columnized {
	column-gap: 2em;
	column-count: 1;
	column-rule: 1px solid var(--dem-base-border);

	@media (min-width: 1264px) { // Vuetify "lg" breakpoint
		columns: 2;
	}
}

.dem-fieldset {
	margin-top: 8px;
	margin-bottom: 8px;

	.theme--light & {
		border-top: 1px solid var(--dem-base-border);
	}

	.theme--dark & {
		border-top: 1px solid rgba(255, 255, 255, 0.12);
	}

	&:first-of-type {
		border-top: 0;
	}
}

.dem-fieldlabel {
	font-size: 12px;

	.theme--light & {
		color: rgba(0, 0, 0, 0.54);
	}
}

// TODO: when https://github.com/vuetifyjs/vuetify/issues/2541 is resolved, switch to it
#demyo .v-pagination button {
	box-shadow: none;
	border-radius: 50%;
}

/** Overlays for icons. */
.dem-overlay-add,
.dem-overlay-edit,
.dem-overlay-delete {
	&.v-icon {
		position: relative;
	}

	&::after {
		font: normal normal normal 67%/1 "Material Design Icons";
		text-rendering: auto;
		position: absolute;
		bottom: -2px;
		right: -4px;
		// TODO [dark]: handle shadows on dark theme
		text-shadow:
			-2px -2px 0 #fff,
			2px -2px #fff,
			-2px 2px #fff,
			2px 2px #fff,
			0 -2px #fff,
			0 2px #fff,
			2px 0 #fff,
			-2px 0 #fff;
	}
}

.dem-overlay-add::after {
	content: "\F0217";
	font-size: 58%;
}

.dem-overlay-edit::after {
	content: "\F3EB";
}

.dem-overlay-delete::after {
	content: "\F1C0"; // Minus is \F374
}

</style>
