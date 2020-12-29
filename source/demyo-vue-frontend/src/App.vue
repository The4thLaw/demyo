<template>
	<!-- TODO: remove dependency to normalize ? -->
	<v-app id="demyo">
		<v-navigation-drawer
			v-if="!$vuetify.breakpoint.smAndDown"
			v-model="displayDetailsPane" right app clipped
			width="33vw" :mobile-break-point="$vuetify.breakpoint.thresholds.sm"
			:disable-resize-watcher="true"
		>
			<portal-target name="appSidePane" />
		</v-navigation-drawer>

		<v-navigation-drawer v-model="mainMenu" app temporary width="20em">
			<v-list class="c-App__menuList">
				<v-list-group v-if="readerLoaded">
					<template #activator>
						<v-list-item-icon>
							<LetterIcon :letter="currentReader.name[0]" :color="currentReader.colour" />
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title>{{ currentReader.name }}</v-list-item-title>
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
					<v-list-item :href="`/readers/`">
						<v-list-item-icon>
							<v-icon>mdi-account-multiple</v-icon>
						</v-list-item-icon>
						<v-list-item-content>
							<v-list-item-title>{{ $t('menu.reader.manage') }}</v-list-item-title>
						</v-list-item-content>
					</v-list-item>
				</v-list-group>

				<v-divider />

				<v-list-item id="c-App__menuSearch">
					<v-list-item-content>
						<v-text-field
							v-model="quicksearchQuery" clearable hide-details
							prepend-icon="mdi-magnify" @keyup="performSearch"
							@click:clear="clearSearch"
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
		<v-app-bar color="primary" dark app clipped-right>
			<v-app-bar-nav-icon @click.stop="mainMenu = !mainMenu" />
			<v-toolbar-title>{{ pageTitle }}</v-toolbar-title>
			<v-spacer />
			<template v-if="!suppressSearch">
				<v-btn id="c-App__toolbarSearchButton" icon @click="showQuicksearch = true; focus()">
					<v-icon>mdi-magnify</v-icon>
				</v-btn>
				<v-expand-x-transition>
					<div v-show="showQuicksearch" id="c-App__toolbarSearchField">
						<v-text-field
							ref="toolbarSearch"
							v-model="quicksearchQuery" clearable hide-details
							@click:clear="showQuicksearch = false; clearSearch()"
							@blur="blur" @keyup="performSearch"
						/>
					</div>
				</v-expand-x-transition>
			</template>
			<portal-target name="appBarAddons" />
			<portal-target name="appTasks" />
		</v-app-bar>

		<v-content id="c-App__mainContent">
			<!-- First part of the details pane management -->
			<v-dialog v-if="$vuetify.breakpoint.smAndDown" v-model="displayDetailsPane">
				<v-card>
					<portal-target name="appSidePane" />
				</v-card>
			</v-dialog>
			<v-container id="c-App__mainContainer" fluid>
				<ReaderSelection
					v-if="requireReaderSelection || promptReaderSelection" :require-selection="requireReaderSelection"
					@cancel="promptReaderSelection = false" @select="promptReaderSelection = false"
				/>
				<v-overlay absolute z-index="4" :value="globalOverlay" class="c-App__overlay">
					<v-progress-circular :indeterminate="true" color="primary" size="96" width="8" />
					<span class="c-App__overlayText">{{ $t('core.loading') }}</span>
				</v-overlay>
				<QuickSearchResults
					v-if="isRelevantSearchQuery" :results="quicksearchResults"
					:loading="quicksearchLoading" @click="showQuicksearch = false; clearSearch()"
				/>
				<!-- Do it on show so that the element stays alive -->
				<router-view v-show="!isRelevantSearchQuery" />
				<AppSnackbar :shown="displaySnackbar" :message="snackbarMessage" @close="closeSnackbar" />
			</v-container>
		</v-content>
		<v-footer color="secondary" dark>
			<v-col>
				Demyo "{{ demyoCodename }}"
			</v-col>
		</v-footer>
	</v-app>
</template>

<script>
import { mapState } from 'vuex'
import { demyoCodename } from '@/myenv'
import AppSnackbar from '@/components/AppSnackbar'
import LetterIcon from '@/components/LetterIcon'
import ReaderSelection from '@/components/ReaderSelection'
import quicksearch from '@/mixins/quicksearch'

export default {
	name: 'DemyoApp',

	components: {
		AppSnackbar,
		LetterIcon,
		ReaderSelection
	},

	mixins: [quicksearch],

	metaInfo() {
		const self = this

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
			demyoCodename: demyoCodename,

			mainMenu: false,
			showQuicksearch: false,

			promptReaderSelection: false,

			menuItems: [
				{
					title: 'menu.section.view',
					active: true,
					subItems: [
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
						},
						{
							title: 'menu.derivatives.add',
							icon: 'mdi-image-frame dem-overlay-add',
							url: '/derivatives/new'
						},
						{
							title: 'menu.images.add',
							icon: 'mdi-camera dem-overlay-add',
							url: '/images/new'
						},
						{
							title: 'menu.tags.add',
							icon: 'mdi-tag dem-overlay-add',
							url: '/tags/new'
						},
						{
							title: 'menu.publishers.add',
							icon: 'mdi-office-building dem-overlay-add',
							url: '/publishers/new'
						},
						{
							title: 'menu.collections.add',
							// Or picture-in-picture-bottom-right if we keep Demyo 2.0/2.1
							icon: 'mdi-folder-multiple dem-overlay-add',
							url: '/collections/new'
						},
						{
							title: 'menu.derivative_sources.add',
							icon: 'mdi-tooltip-account dem-overlay-add',
							url: '/derivativeSources/new'
						},
						{
							title: 'menu.derivative_types.add',
							icon: 'mdi-brush dem-overlay-add',
							url: '/derivativeTypes/new'
						},
						{
							title: 'menu.bindings.add',
							icon: 'mdi-notebook dem-overlay-add',
							url: '/bindings/new'
						}
					]
				},
				{
					title: 'menu.section.management',
					subItems: [
						// Advanced search would go here
						{
							title: 'menu.manage.export',
							icon: 'mdi-download',
							url: '/manage/export'
						},
						{
							title: 'menu.manage.import',
							icon: 'mdi-upload',
							url: '/manage/import'
						},
						// Stats, configure, orphans and help would go here
						{
							title: 'menu.manage.about',
							icon: 'mdi-information',
							url: '/about'
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
			displayDetailsPane: state => state.ui.displayDetailsPane
		})
	},

	methods: {
		closeSnackbar() {
			this.$store.dispatch('ui/closeSnackbar')
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
:root {
	// TODO[dark]: alternate colors
	--dem-text: rgba(0, 0, 0, 0.87);
	--dem-text-lighter: rgba(0, 0, 0, 0.54);
	--dem-primary-contrast: #fff;
	--dem-bg-contrast: #ddd;
	--dem-base-border: #ccc;
}

html[lang],
#demyo {
	font-family: BlinkMacSystemFont, -apple-system, Roboto, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
	background: #eee;
}

#demyo .c-App__overlay .v-overlay__content {
	position: absolute;
	top: 3em;
}

#c-App__toolbarSearchField {
	max-width: 20em;
}

@media screen and (max-width: 600px) { // Vuetify "xs" breakpoint
	#c-App__toolbarSearchField,
	#c-App__toolbarSearchButton {
		display: none;
	}
}

@media screen and (min-width: 600px) {
	#c-App__menuSearch {
		display: none;
	}
}

#c-App__mainContent > .v-content__wrap {
	display: flex;
	flex-direction: column;
}

#c-App__mainContainer {
	flex: 1;
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
		// https://css-tricks.com/when-do-you-use-inline-block/#column-children-that-dont-break-in-the-middle-of-themselves
		display: inline-block;
		width: 100%; // Else we may get more than one item per line in a single columns
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
.dem-overlay-add::after {
	content: "\F0415";
	font-size: 58%;
}

.dem-overlay-edit::after {
	content: "\F03EB";
}

.dem-overlay-delete::after {
	content: "\F01B4"; // Minus could also be used
}

.dem-overlay-check::after {
	content: "\F012C";
}

.dem-overlay-add,
.dem-overlay-edit,
.dem-overlay-delete,
.dem-overlay-check {
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

	.v-btn--fab &::after {
		bottom: -5px;
	}

	.v-btn.accent &::after {
		--icon-outline-color: var(--v-accent-base);
	}
}

.dem-multiline-value {
	white-space: pre;
}

</style>
