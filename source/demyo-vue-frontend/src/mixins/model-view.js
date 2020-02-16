export default {
	data() {
		return {
			loading: true,
			parsedId: undefined
		}
	},

	watch: {
		'$route': 'fetchDataInternal'
	},

	created() {
		// Consistency checks for dependencies of this mixin
		if (!this.fetchData) {
			throw new Error('Missing fetchData method')
		}

		// Set the ID
		this.parsedId = parseInt(this.$route.params.id, 10)
		this.fetchDataInternal()
	},

	methods: {
		async fetchDataInternal() {
			this.loading = true

			await this.fetchData()

			this.loading = false
		}
	}
}
