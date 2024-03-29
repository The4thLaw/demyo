<template>
	<v-app id="demyo">
		<v-navigation-drawer
			v-if="!$vuetify.breakpoint.smAndDown"
			v-model="displayDetailsPane" right app clipped
			width="33vw" mobile-breakpoint="sm"
			:disable-resize-watcher="true"
		>
			<portal-target name="appSidePane" />
		</v-navigation-drawer>

		<v-navigation-drawer v-model="mainMenu" app temporary width="20em">
			<v-list class="l-DefaultLayout__menuList">
				<v-list-group v-if="readerLoaded">
					<template #activator>
						<v-list-item-icon>
							<LetterIcon :letter="currentReader.identifyingName[0]" :color="currentReader.colour" />
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title>{{ currentReader.identifyingName }}</v-list-item-title>
						</v-list-item-content>
					</template>

					<v-list-item :to="`/readers/${currentReader.id}/favourites`">
						<v-list-item-icon>
							<v-icon>mdi-heart</v-icon>
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title>{{ $t('menu.reader.faves') }}</v-list-item-title>
						</v-list-item-content>
					</v-list-item>
					<v-list-item :to="`/readers/${currentReader.id}/readingList`">
						<v-list-item-icon>
							<v-icon>mdi-library</v-icon>
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title>{{ $t('menu.reader.readingList') }}</v-list-item-title>
						</v-list-item-content>
					</v-list-item>
					<v-list-item :to="`/readers/${currentReader.id}/configuration`">
						<v-list-item-icon>
							<v-icon>mdi-cog</v-icon>
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title>{{ $t('menu.reader.configuration') }}</v-list-item-title>
						</v-list-item-content>
					</v-list-item>
					<v-list-item @click="promptReaderSelection = true">
						<v-list-item-icon>
							<v-icon>mdi-account-convert</v-icon>
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title>{{ $t('menu.reader.switch') }}</v-list-item-title>
						</v-list-item-content>
					</v-list-item>
					<v-list-item :to="`/readers/`">
						<v-list-item-icon>
							<v-icon>mdi-account-multiple</v-icon>
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title>{{ $t('menu.reader.manage') }}</v-list-item-title>
						</v-list-item-content>
					</v-list-item>
				</v-list-group>

				<v-divider />

				<v-list-item id="l-DefaultLayout__menuSearch">
					<v-list-item-content>
						<v-text-field
							ref="menuSearch"
							v-model="quicksearchQuery" clearable hide-details
							autocomplete="off"
							prepend-icon="mdi-magnify" @keyup="performSearch"
							@click:clear="clearSearch" @keydown.enter="enterSearch"
						/>
					</v-list-item-content>
				</v-list-item>

				<v-list-item to="/">
					<v-list-item-icon>
						<v-icon>mdi-home</v-icon>
					</v-list-item-icon>
					<v-list-item-content>
						<v-list-item-title>{{ $t('menu.main.home') }}</v-list-item-title>
					</v-list-item-content>
				</v-list-item>

				<v-list-group v-for="section in menuItems" :key="section.title" v-model="section.active">
					<template #activator>
						<v-list-item-content>
							<v-list-item-title v-text="$t(section.title)" />
						</v-list-item-content>
					</template>

					<v-list-item v-for="item in section.subItems" :key="item.title" :to="item.url" exact>
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
		<v-app-bar color="primary" dark app clipped-right>
			<v-app-bar-nav-icon @click.stop="mainMenu = !mainMenu" />
			<v-toolbar-title>{{ pageTitle }}</v-toolbar-title>
			<v-spacer />
			<template v-if="!suppressSearch">
				<v-btn id="l-DefaultLayout__toolbarSearchButton" icon @click="showQuicksearch = true; focus()">
					<v-icon>mdi-magnify</v-icon>
				</v-btn>
				<v-expand-x-transition>
					<div v-show="showQuicksearch" id="l-DefaultLayout__toolbarSearchField">
						<v-text-field
							ref="toolbarSearch"
							v-model="quicksearchQuery" clearable hide-details
							autocomplete="off"
							@click:clear="showQuicksearch = false; clearSearch()"
							@blur="blur" @keyup="performSearch" @keydown.enter="enterSearch"
						/>
					</div>
				</v-expand-x-transition>
			</template>
			<portal-target name="appBarAddons" />
			<portal-target name="appTasks" />
		</v-app-bar>

		<v-main id="l-DefaultLayout__mainContent">
			<!-- First part of the details pane management -->
			<v-dialog v-if="$vuetify.breakpoint.smAndDown" v-model="displayDetailsPane">
				<v-card>
					<portal-target name="appSidePane" />
				</v-card>
			</v-dialog>
			<v-container id="l-DefaultLayout__mainContainer" fluid>
				<ReaderSelection
					v-if="readerSelectionRequired || promptReaderSelection" :require-selection="readerSelectionRequired"
					@cancel="promptReaderSelection = false" @select="promptReaderSelection = false"
				/>
				<v-overlay absolute z-index="4" :value="globalOverlay" class="l-DefaultLayout__overlay">
					<v-progress-circular :indeterminate="true" color="primary" size="96" width="8" />
					<span class="l-DefaultLayout__overlayText">{{ $t('core.loading') }}</span>
				</v-overlay>
				<QuickSearchResults
					v-if="isRelevantSearchQuery" :results="quicksearchResults"
					:loading="quicksearchLoading" @click="showQuicksearch = false; clearSearch()"
				/>
				<!-- Do it on show so that the element stays alive. v-show doesn't work on slot so use a div -->
				<div v-show="!isRelevantSearchQuery" id="l-DefaultLayout__routerView">
					<slot />
				</div>
				<AppSnackbar :shown="displaySnackbar" :message="snackbarMessage" @close="closeSnackbar" />
			</v-container>
		</v-main>
		<v-footer color="secondary" dark>
			<v-col>
				Demyo "{{ demyoCodename }}"
			</v-col>
		</v-footer>
	</v-app>
</template>

<script>
import AppSnackbar from '@/components/AppSnackbar.vue'
import LetterIcon from '@/components/LetterIcon.vue'
import ReaderSelection from '@/components/ReaderSelection.vue'
import quicksearch from '@/mixins/quicksearch'
import { demyoCodename } from '@/myenv'
import { useReaderStore } from '@/stores/reader'
import { useUiStore } from '@/stores/ui'
import { mapState, mapWritableState } from 'pinia'
import defaultMenu from './default-menu.json'

export default {
	name: 'DefaultLayout',

	components: {
		AppSnackbar,
		LetterIcon,
		ReaderSelection
	},

	mixins: [quicksearch],

	metaInfo() {
		const self = this

		return {
			changed(newInfo, addedTags, removedTags) {
				self.pageTitle = newInfo.titleChunk
			}
		}
	},

	data() {
		return {
			uiStore: useUiStore(),

			pageTitle: 'Demyo',
			demyoCodename: demyoCodename,

			mainMenu: false,
			showQuicksearch: false,

			promptReaderSelection: false,

			menuItems: defaultMenu
		}
	},

	computed: {
		...mapState(useUiStore, ['suppressSearch', 'globalOverlay', 'displaySnackbar']),
		...mapWritableState(useUiStore, ['displayDetailsPane']),
		...mapState(useUiStore, {
			snackbarMessage: store => store.snackbarMessages[0]
		}),
		...mapState(useReaderStore, ['readerLoaded', 'readerSelectionRequired', 'currentReader'])
	},

	watch: {
		$route() {
			// Route changed, clear the quick search and collapse the field
			this.clearSearch()
			this.blur()
			// Restore the menu
			this.menuItems[0].active = true
			for (let i = 1; i < this.menuItems.length; i++) {
				this.menuItems[i].active = false
			}
		}
	},

	methods: {
		enterSearch() {
			this.$refs.menuSearch.blur()
			this.$refs.toolbarSearch.blur()
			this.performSearch()
		},

		closeSnackbar() {
			this.uiStore.closeSnackbar()
		},

		focus() {
			this.$refs.toolbarSearch.focus()
			// Vuetify doesn't forward the Vue transition events so we delay a refocus
			window.setTimeout(() => this.$refs.toolbarSearch.focus(), 300)
		},

		blur() {
			this.showQuicksearch = !!this.quicksearchQuery
		}
	}
}
</script>

<style lang="less">
html[lang],
#demyo {
	font-family:
		BlinkMacSystemFont,
		-apple-system,
		Roboto,
		Arial,
		sans-serif,
		"Apple Color Emoji",
		"Segoe UI Emoji",
		"Segoe UI Symbol";
	// TODO [dark]: handle the dark theme.
	// Maybe use the overrides listed on https://github.com/vuetifyjs/vuetify/releases/tag/v2.2.6 ?
	background: #eee;
}

#demyo .l-DefaultLayout__overlay .v-overlay__content {
	position: absolute;
	top: 3em;
}

#l-DefaultLayout__toolbarSearchField {
	max-width: 20em;
}

@media screen and (max-width: 600px) { // Vuetify "xs" breakpoint
	#l-DefaultLayout__toolbarSearchField,
	#l-DefaultLayout__toolbarSearchButton {
		display: none;
	}
}

@media screen and (min-width: 600px) {
	#l-DefaultLayout__menuSearch {
		display: none;
	}
}

@media (max-width: 435px) {
	#l-DefaultLayout__mainContainer,
	#l-DefaultLayout__mainContainer > .container,
	#l-DefaultLayout__routerView > .container,
	.container.v-TagIndex__list {
		padding-left: 0;
		padding-right: 0;
	}
}

.l-DefaultLayout__overlayText {
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
	text-decoration-skip-ink: all;

	&:not(.v-btn):hover {
		text-decoration: underline;
	}
}

.v-application .l-DefaultLayout__menuList,
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

#demyo .v-input--is-readonly:not(.v-input--is-disabled) input,
#demyo .v-input--checkbox.v-input--is-readonly:not(.v-input--is-disabled) label {
	color: var(--dem-bg-contrast);
}

.dem-columnized {
	column-gap: 2em;
	column-count: 1;
	column-rule: 1px solid var(--dem-base-border);

	@media (min-width: 1264px) { // Vuetify "lg" breakpoint
		columns: 2;
	}

	> * {
		// Never break the main item of a columnized layout
		// This notably fixes a Webkit issue where text would be horizontally split
		// between two columns (top part of the letters in one column and bottom
		// part in the other)
		break-inside: avoid;
		// The following also solves some issues with margins spanning across columns
		// https://css-tricks.com/when-do-you-use-inline-block/
		display: inline-block !important;
		width: 100%; // Else we may get more than one item per line in a single columns
	}
}

.dem-fieldset {
	margin-top: 16px;
	margin-bottom: 8px;
	padding-top: 16px;

	.theme--light & {
		border-top: 1px solid var(--dem-base-border);
	}

	.theme--dark & {
		border-top: 1px solid rgba(255, 255, 255, 0.12);
	}

	&:first-of-type {
		border-top: 0;
		padding-top: 0;
	}
}

.dem-fieldlabel {
	font-size: 12px;

	.theme--light & {
		color: rgba(0, 0, 0, 0.54);
	}
}

#demyo {
	// TODO: when https://github.com/vuetifyjs/vuetify/issues/2541 is resolved, switch to it
	.v-pagination button {
		box-shadow: none;
		border-radius: 50%;
	}

	/** Overlays for icons. */
	.v-icon.dem-overlay-add::after {
		content: "\F0415";
		font-size: 58%;
	}

	.v-icon.dem-overlay-edit::after {
		content: "\F03EB";
	}

	.v-icon.dem-overlay-delete::after {
		content: "\F01B4"; // Minus could also be used
	}

	.dem-overlay-check::after {
		content: "\F012C";
	}

	.v-icon.dem-overlay-add,
	.v-icon.dem-overlay-edit,
	.v-icon.dem-overlay-delete,
	.v-icon.dem-overlay-check {
		&.v-icon {
			position: relative;
		}

		&::after {
			// We need to completely override the default styles from Vuetify to be able to do this
			opacity: initial;
			top: initial;
			left: initial;
			background-color: initial;
			border-radius: initial;
			display: initial;
			height: initial;
			transform: initial;
			pointer-events: initial;
			width: initial;
			// Here are our styles
			font: normal normal normal 67%/1 "Material Design Icons";
			text-rendering: auto;
			position: absolute;
			bottom: -4px;
			right: -4px;
			// TODO [dark]: handle shadows on dark theme
			--icon-outline-color: #fff;
			text-shadow:
				-2px -2px var(--icon-outline-color),
				-2px -1px var(--icon-outline-color),
				-2px 0 var(--icon-outline-color),
				-2px 1px var(--icon-outline-color),
				-2px 2px var(--icon-outline-color),
				2px -2px var(--icon-outline-color),
				2px -1px var(--icon-outline-color),
				2px 0 var(--icon-outline-color),
				2px 1px var(--icon-outline-color),
				2px 2px var(--icon-outline-color),
				-1px -2px var(--icon-outline-color),
				0 -2px var(--icon-outline-color),
				1px -2px var(--icon-outline-color),
				-1px 2px var(--icon-outline-color),
				0 2px var(--icon-outline-color),
				1px 2px var(--icon-outline-color);
		}
	}

	.v-btn--fab {
		.v-icon.dem-overlay-add::after,
		.v-icon.dem-overlay-edit::after,
		.v-icon.dem-overlay-delete::after,
		.v-icon.dem-overlay-check::after {
			bottom: -5px;
		}
	}

	.v-btn.accent {
		.v-icon.dem-overlay-add::after,
		.v-icon.dem-overlay-edit::after,
		.v-icon.dem-overlay-delete::after,
		.v-icon.dem-overlay-check::after {
			--icon-outline-color: var(--v-accent-base);
		}
	}
}

.dem-multiline-value {
	white-space: pre;
}
</style>
