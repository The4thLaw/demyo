<template>
	<!-- Force it to always look like a link -->
	<v-list-item
		class="v-list-item--link" :to="to"
		:prepend-icon="icon" :title="label"
		@click.stop="handleClick"
	>
		<v-dialog v-model="confirmationDialog" max-width="50%">
			<v-card>
				<v-card-title>
					{{ $t('quickTasks.confirm.title') }}
				</v-card-title>
				<v-card-text>
					{{ confirm }}
				</v-card-text>

				<v-card-actions>
					<v-spacer />

					<v-btn
						color="secondary" class="c-AppTask__confirm" variant="elevated"
						@click="confirmationDialog = false; $emit('confirm')"
					>
						{{ $t('quickTasks.confirm.ok.label') }}
					</v-btn>

					<v-btn color="primary" @click="confirmationDialog = false; $emit('cancel')">
						{{ $t('quickTasks.confirm.cancel.label') }}
					</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>
	</v-list-item>
</template>

<script>
export default {
	name: 'AppTask',

	props: {
		label: {
			type: String,
			required: true
		},

		icon: {
			type: String,
			required: true
		},

		to: {
			type: null,
			default: undefined
		},

		confirm: {
			type: String,
			default: ''
		}
	},

	data() {
		return {
			confirmationDialog: false
		}
	},

	methods: {
		handleClick(e) {
			this.$emit('click', e)
			if (this.confirm) {
				this.confirmationDialog = true
			}
		}
	}
}
</script>
