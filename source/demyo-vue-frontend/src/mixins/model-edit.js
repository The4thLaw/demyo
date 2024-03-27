import { useUiStore } from '@/stores/ui'

export default {
	data() {
		return {
			initialized: false,
			parsedId: undefined
		}
	},

	computed: {
		pageTitle() {
			return this.initialized
				? (this.parsedId
					? this.$t(this.mixinConfig.modelEdit.titleKeys.edit)
					: this.$t(this.mixinConfig.modelEdit.titleKeys.add))
				: null
		}
	},

	watch: {
		$route: 'fetchDataInternal'
	},

	created() {
		// Consistency checks for dependencies of this mixin
		if (!this.mixinConfig.modelEdit.titleKeys.add) {
			throw new Error('Missing titleKey for add')
		}
		if (!this.mixinConfig.modelEdit.titleKeys.edit) {
			throw new Error('Missing titleKey for edit')
		}
		if (!this.mixinConfig.modelEdit.saveRedirectViewName) {
			throw new Error('Missing redirect view for save action')
		}
		if (!this.fetchData) {
			throw new Error('Missing fetchData method')
		}
		if (!this.saveHandler) {
			throw new Error('Missing save handler')
		}

		useUiStore().disableSearch()
		this.fetchDataInternal()
	},

	mounted() {
		// Consistency checks for dependencies of this mixin
		if (!this.$refs.form) {
			throw new Error('Missing form $ref')
		}
	},

	methods: {
		async fetchDataInternal() {
			if (this.$route.params.id) { // Edit mode -> set the ID
				this.parsedId = parseInt(this.$route.params.id, 10)
			}

			this.initialized = false
			const uiStore = useUiStore()
			uiStore.enableGlobalOverlay()

			const loadPromises = []

			loadPromises.push(this.fetchData())

			// Integration with other mixins
			if (this.refreshAuthors) {
				loadPromises.push(this.refreshAuthors())
			}
			if (this.refreshImages) {
				loadPromises.push(this.refreshImages())
			}
			if (this.refreshPublishers) {
				loadPromises.push(this.refreshPublishers())
			}
			if (this.refreshTags) {
				loadPromises.push(this.refreshTags())
			}

			await Promise.all(loadPromises)

			this.initialized = true
			uiStore.disableGlobalOverlay()
		},

		async save() {
			if (!this.$refs.form.validate()) {
				return
			}
			const uiStore = useUiStore()
			uiStore.enableGlobalOverlay()
			const id = await this.saveHandler()
			uiStore.disableGlobalOverlay()
			if (id <= 0) {
				uiStore.showSnackbar(this.$t('core.exception.api.title'))
			} else {
				this.$router.push({ name: this.mixinConfig.modelEdit.saveRedirectViewName, params: { id: id } })
			}
		},

		reset() {
			this.$refs.form.reset()
			if (this.parsedId) {
				this.fetchDataInternal()
			}
		}
	}
}
