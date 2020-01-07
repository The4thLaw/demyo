export default {
	metaInfo() {
		return {
			title: this.initialized
				? (this.author.id
					? this.$t(this.mixinConfig.modelEdit.titleKeys.edit)
					: this.$t(this.mixinConfig.modelEdit.titleKeys.edit))
				: ''
		}
	},

	data() {
		return {
			initialized: false,
			parsedId: undefined
		}
	},

	watch: {
		'$route': 'fetchDataInternal'
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

		this.$store.dispatch('ui/disableSearch')
		if (this.$route.params.id) { // Edit mode -> set the ID
			this.parsedId = parseInt(this.$route.params.id, 10)
		}
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
			this.initialized = false
			this.$store.dispatch('ui/enableGlobalOverlay')
			await this.fetchData()
			this.initialized = true
			this.$store.dispatch('ui/disableGlobalOverlay')
		},

		async save() {
			if (!this.$refs.form.validate()) {
				return
			}
			this.$store.dispatch('ui/enableGlobalOverlay')
			let id = await this.saveHandler()
			this.$store.dispatch('ui/disableGlobalOverlay')
			if (id <= 0) {
				this.$store.dispatch('ui/showSnackbar', this.$t('core.exception.api.title'))
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
