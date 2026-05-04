<template>
	<div class="c-Autocomplete">
		<v-autocomplete
			v-model="model"
			v-model:search="search"
			v-bind="$attrs"
			:label="$t(labelKey, multiple ? 2 : 1)"
			:item-title="itemTitle"
			item-value="id"
			:multiple="multiple"
			:chips="multiple"
			:closable-chips="multiple"
			:loading="loading ? 'primary' : false"
			:no-data-text="$t('core.components.Autocomplete.nodata')"
			@update:model-value="onUpdateSelection"
		>
			<template v-if="refreshable || addComponent" #append>
				<v-btn
					v-if="addComponent"
					icon size="small" variant="flat" @click.stop="showAddDialog = true"
				>
					<v-icon>mdi-plus</v-icon>
				</v-btn>
				<v-btn
					v-if="refreshable"
					icon size="small" variant="flat" @click.stop="emit('refresh')"
				>
					<v-icon>mdi-refresh</v-icon>
				</v-btn>
			</template>
		</v-autocomplete>
		<v-dialog v-if="addComponent" v-model="showAddDialog" max-width="max(50vw, 45em)">
			<v-card>
				<v-card-title>
					{{ $t(addLabel) }}
				</v-card-title>
				<v-card-text>
					<component
						:is="addComponent" mode="minimal" teleport-actions=".c-Autocomplete__dialog-actions"
						v-bind="addProps" @save="saved"
					/>
				</v-card-text>

				<v-card-actions>
					<v-spacer />
					<!-- Placeholder for the form actions -->
					<div class="c-Autocomplete__dialog-actions" />
					<!-- Our own cancel -->
					<v-btn color="primary" @click="showAddDialog = false">
						{{ $t('quickTasks.confirm.cancel.label') }}
					</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>
	</div>
</template>

<script setup lang="ts">
import type { Component } from 'vue'

const model = defineModel<unknown>()

withDefaults(defineProps<{
	labelKey: string
	itemTitle?: string
	refreshable?: boolean
	loading?: boolean
	multiple?: boolean,
	addComponent?: Component,
	addProps?: unknown,
	addLabel?: string
}>(), {
	itemTitle: 'identifyingName',
	refreshable: false,
	loading: false,
	multiple: false,
	addComponent: undefined,
	addProps: undefined,
	addLabel: undefined
})

const search = ref('')
const showAddDialog = ref(false)

const emit = defineEmits<{
	refresh: [],
	change: [],
	added: [id: number]
}>()

function onUpdateSelection(): void {
	search.value = ''
	emit('change')
}

function saved(id: number): void {
	console.debug(`Saved new Autocomplete entry with id ${id}, will refresh and add`)
	emit('refresh')
	emit('added', id)
	showAddDialog.value = false
}
</script>
