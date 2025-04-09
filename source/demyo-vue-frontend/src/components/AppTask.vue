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
						@click="confirmationDialog = false; emit('confirm')"
					>
						{{ $t('quickTasks.confirm.ok.label') }}
					</v-btn>

					<v-btn color="primary" @click="confirmationDialog = false; emit('cancel')">
						{{ $t('quickTasks.confirm.cancel.label') }}
					</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>
	</v-list-item>
</template>

<script setup lang="ts">
const props = defineProps<{
	label: string,
	icon: string,
	to?: string | { name: string, query: Record<string, unknown> }
	confirm?: string
}>()

const emit = defineEmits(['click', 'confirm', 'cancel'])

const confirmationDialog = ref(false)

function handleClick(e: MouseEvent): void {
	emit('click', e)
	if (props.confirm) {
		confirmationDialog.value = true
	}
}
</script>
