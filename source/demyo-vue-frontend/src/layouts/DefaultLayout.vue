<template>
	<v-app :class="$vuetify.theme['current'].dark?'dem-theme--dark':'dem-theme--light'">
		<v-navigation-drawer
			v-if="!$vuetify.display.smAndDown"
			v-model="displayDetailsPane" location="right"
			width="33vw" mobile-breakpoint="sm"
			:disable-resize-watcher="true"
		>
			<div id="teleport-appSidePane" />
		</v-navigation-drawer>

		<!-- Once https://github.com/vuetifyjs/vuetify/issues/16150 is resolved, switch back to 20em -->
		<v-navigation-drawer v-model="mainMenu" width="320" temporary>
			<v-list :opened="['menu.section.view']" class="l-DefaultLayout__menuList">
				<v-list-group v-if="readerLoaded" value="Reader" color="primary">
					<template #activator="{ props }">
						<v-list-item v-bind="props" :title="currentReader.identifyingName">
							<template #prepend>
								<LetterIcon :letter="currentReader.identifyingName[0]" :color="currentReader.colour" />
							</template>
						</v-list-item>
					</template>

					<v-list-item
						:to="`/readers/${currentReader.id}/favourites`"
						prepend-icon="mdi-heart" :title="$t('menu.reader.faves')"
					/>
					<v-list-item
						:to="`/readers/${currentReader.id}/readingList`"
						prepend-icon="mdi-library" :title="$t('menu.reader.readingList')"
					/>
					<v-list-item
						:to="`/readers/${currentReader.id}/configuration`"
						prepend-icon="mdi-cog" :title="$t('menu.reader.configuration')"
					/>
					<v-list-item
						prepend-icon="mdi-account-convert" :title=" $t('menu.reader.switch')"
						@click="promptReaderSelection = true"
					/>
					<v-list-item
						:to="`/readers/`"
						prepend-icon="mdi-account-multiple" :title="$t('menu.reader.manage')"
					/>
				</v-list-group>

				<v-divider />

				<v-list-item id="l-DefaultLayout__menuSearch">
					<v-text-field
						ref="menuSearch"
						v-model="quicksearchQuery" clearable hide-details
						autocomplete="off"
						prepend-icon="mdi-magnify" @keyup="performSearch"
						@click:clear="clearSearch" @keydown.enter="enterSearch"
					/>
				</v-list-item>

				<v-list-item to="/" prepend-icon="mdi-home" :title="$t('menu.main.home')" />

				<v-list-group
					v-for="section in menuItems" :key="section.title" :value="section.title" color="primary"
				>
					<template #activator="{ props }">
						<v-list-item v-bind="props" :title="$t(section.title)" />
					</template>

					<v-list-item
						v-for="item in section.subItems" :key="item.title"
						:to="item.url"
						:prepend-icon="item.icon" :title="$t(item.title)"
						exact
					/>
				</v-list-group>
			</v-list>
		</v-navigation-drawer>
		<v-app-bar color="primary" class="pe-4">
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
							:color="$vuetify.theme['current'].colors['on-primary']"
							@click:clear="showQuicksearch = false; clearSearch()"
							@blur="blur" @keyup="performSearch" @keydown.enter="enterSearch"
						/>
					</div>
				</v-expand-x-transition>
			</template>
			<div id="teleport-appBarAddons" />
			<div id="teleport-appTasks" />
		</v-app-bar>

		<v-main id="l-DefaultLayout__mainContent">
			<!-- First part of the details pane management -->
			<v-dialog v-if="$vuetify.display.smAndDown" v-model="displayDetailsPane">
				<v-card>
					<div id="teleport-appSidePane" />
				</v-card>
			</v-dialog>
			<v-container id="l-DefaultLayout__mainContainer" fluid>
				<ReaderSelection
					v-if="readerSelectionRequired || promptReaderSelection" :require-selection="readerSelectionRequired"
					@cancel="promptReaderSelection = false" @select="promptReaderSelection = false"
				/>
				<v-overlay absolute z-index="4" :model-value="globalOverlay" class="align-center justify-center">
					<v-progress-circular :indeterminate="true" color="primary" size="96" width="8" />
					<span class="l-DefaultLayout__overlayText">{{ $t('core.loading') }}</span>
				</v-overlay>
				<QuickSearchResults
					v-if="isRelevantSearchQuery" :results="quicksearchResults"
					:loading="quicksearchLoading" @navigate="showQuicksearch = false; clearSearch()"
				/>
				<!-- Do it on show so that the element stays alive. v-show doesn't work on slot so use a div -->
				<div v-show="!isRelevantSearchQuery" id="l-DefaultLayout__routerView">
					<slot />
				</div>
				<AppSnackbar :shown="displaySnackbar" :message="snackbarMessage" @close="closeSnackbar" />
			</v-container>
		</v-main>
		<v-footer color="surface-variant">
			<v-col>
				Demyo "{{ demyoCodename }}"
			</v-col>
		</v-footer>
	</v-app>
</template>

<script>
import quicksearch from '@/mixins/quicksearch'
import { demyoCodename } from '@/myenv'
import { useReaderStore } from '@/stores/reader'
import { useUiStore } from '@/stores/ui'
import { mapState, mapWritableState } from 'pinia'
import defaultMenu from './default-menu.json'

export default {
	name: 'DefaultLayout',

	mixins: [quicksearch],

	data() {
		return {
			uiStore: useUiStore(),

			pageTitle: 'Demyo',
			pageTitleObserver: null,
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
		}
	},

	mounted() {
		// Monitor the page title for changes. unhead doesn't seem to have an event for this
		this.pageTitleObserver = new MutationObserver((mutations) => {
			const pageTitle = mutations[0].target.text.replace(/ – Demyo$/, '')
			this.pageTitle = pageTitle
		})
		this.pageTitleObserver.observe(
			document.querySelector('title'),
			{ subtree: true, characterData: true, childList: true }
		)
	},

	unmounted() {
		this.pageTitleObserver.disconnect()
		this.pageTitleObserver = null
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

<style lang="scss">
.l-DefaultLayout__overlayText {
	font-size: 1.5rem;
	padding-left: 1em;
	color: rgb(var(--v-theme-on-primary));
}

#l-DefaultLayout__toolbarSearchField {
	width: 20em;

	input, .v-field__clearable {
		padding-top: 0;
		min-height: 0;
	}
}

// Reduce padding in item groups in the menu
#demyo .v-navigation-drawer .v-list-group {
	--prepend-width: 0px;
	--indent-padding: -16px;
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

.v-application .l-DefaultLayout__menuList,
.v-application .c-AppTasks__list {
	a,
	a:hover {
		text-decoration: none;
	}
}
</style>
