<template>
	<!-- Force it to always look like a link -->
	<v-list-item class="v-list-item--link" :to="to" @click.stop="handleClick">
		<v-list-item-icon>
			<v-icon v-text="icon" />
		</v-list-item-icon>
		<v-list-item-content>
			<v-list-item-title>{{ label }}</v-list-item-title>
		</v-list-item-content>
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

					<v-btn color="accent" @click="confirmationDialog = false; $emit('confirm')">
						{{ $t('quickTasks.confirm.ok.label') }}
					</v-btn>

					<v-btn color="primary" text @click="confirmationDialog = false; $emit('cancel')">
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
