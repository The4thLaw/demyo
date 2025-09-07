<template>
	<v-dialog v-model="model" max-width="800px">
		<v-card>
			<v-card-title>
				{{ $t(titleLabel) }}
			</v-card-title>
			<v-card-text>
				<div v-if="mainImageLabel">
					<span class="dem-fieldlabel">
						{{ $t(mainImageLabel) }}
					</span>
					<file-pond
						ref="mainPond"
						name="filePondMainImage"
						accepted-file-types="image/*"
						:server="serverConfig"
						:allow-multiple="false"
						:label-idle="$t('draganddrop.filepond.labelIdle')"
						:label-tap-to-cancel="$t('draganddrop.filepond.labelTapToCancel')"
						:label-tap-to-retry="$t('draganddrop.filepond.labelTapToRetry')"
						:label-tap-to-undo="$t('draganddrop.filepond.labelTapToUndo')"
						:file-validate-type-label-expected-types="
							$t('draganddrop.filepond.fileValidateTypeLabelExpectedTypes')"
						:label-button-abort-item-load="$t('draganddrop.filepond.labelButtonAbortItemLoad')"
						:label-button-abort-item-processing="$t('draganddrop.filepond.labelButtonAbortItemProcessing')"
						:label-button-process-item="$t('draganddrop.filepond.labelButtonProcessItem')"
						:label-button-remove-item="$t('draganddrop.filepond.labelButtonRemoveItem')"
						:label-button-retry-item-load="$t('draganddrop.filepond.labelButtonRetryItemLoad')"
						:label-button-retry-item-processing="$t('draganddrop.filepond.labelButtonRetryItemProcessing')"
						:label-button-undo-item-processing="$t('draganddrop.filepond.labelButtonUndoItemProcessing')"
						:label-file-load-error="$t('draganddrop.filepond.labelFileLoadError')"
						:label-file-loading="$t('draganddrop.filepond.labelFileLoading')"
						:label-file-processing="$t('draganddrop.filepond.labelFileProcessing')"
						:label-file-processing-aborted="$t('draganddrop.filepond.labelFileProcessingAborted')"
						:label-file-processing-complete="$t('draganddrop.filepond.labelFileProcessingComplete')"
						:label-file-processing-error="$t('draganddrop.filepond.labelFileProcessingError')"
						:label-file-size-not-available="$t('draganddrop.filepond.labelFileSizeNotAvailable')"
						:label-file-type-not-allowed="$t('draganddrop.filepond.labelFileTypeNotAllowed')"
						:label-file-waiting-for-size="$t('draganddrop.filepond.labelFileWaitingForSize')"
					/>
				</div>

				<div v-if="otherImagesLabel">
					<span class="dem-fieldlabel">
						{{ $t(otherImagesLabel) }}
					</span>
					<file-pond
						ref="otherPond"
						name="filePondOtherImage"
						accepted-file-types="image/*"
						:server="serverConfig"
						:allow-multiple="true"
						:label-idle="$t('draganddrop.filepond.labelIdle')"
						:label-tap-to-cancel="$t('draganddrop.filepond.labelTapToCancel')"
						:label-tap-to-retry="$t('draganddrop.filepond.labelTapToRetry')"
						:label-tap-to-undo="$t('draganddrop.filepond.labelTapToUndo')"
						:file-validate-type-label-expected-types="
							$t('draganddrop.filepond.fileValidateTypeLabelExpectedTypes')"
						:label-button-abort-item-load="$t('draganddrop.filepond.labelButtonAbortItemLoad')"
						:label-button-abort-item-processing="$t('draganddrop.filepond.labelButtonAbortItemProcessing')"
						:label-button-process-item="$t('draganddrop.filepond.labelButtonProcessItem')"
						:label-button-remove-item="$t('draganddrop.filepond.labelButtonRemoveItem')"
						:label-button-retry-item-load="$t('draganddrop.filepond.labelButtonRetryItemLoad')"
						:label-button-retry-item-processing="$t('draganddrop.filepond.labelButtonRetryItemProcessing')"
						:label-button-undo-item-processing="$t('draganddrop.filepond.labelButtonUndoItemProcessing')"
						:label-file-load-error="$t('draganddrop.filepond.labelFileLoadError')"
						:label-file-loading="$t('draganddrop.filepond.labelFileLoading')"
						:label-file-processing="$t('draganddrop.filepond.labelFileProcessing')"
						:label-file-processing-aborted="$t('draganddrop.filepond.labelFileProcessingAborted')"
						:label-file-processing-complete="$t('draganddrop.filepond.labelFileProcessingComplete')"
						:label-file-processing-error="$t('draganddrop.filepond.labelFileProcessingError')"
						:label-file-size-not-available="$t('draganddrop.filepond.labelFileSizeNotAvailable')"
						:label-file-type-not-allowed="$t('draganddrop.filepond.labelFileTypeNotAllowed')"
						:label-file-waiting-for-size="$t('draganddrop.filepond.labelFileWaitingForSize')"
					/>
				</div>
			</v-card-text>
			<v-card-actions>
				<v-spacer />

				<v-btn color="secondary" variant="elevated" @click="save">
					{{ $t('draganddrop.button.confirm') }}
				</v-btn>

				<v-btn color="primary" variant="text" @click="cancel">
					{{ $t('draganddrop.button.cancel') }}
				</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>

<script setup lang="ts">
import FilePond from '@/helpers/filepond'
import { apiRoot } from '@/myenv'
import type { FilePond as FilePondType } from 'filepond'
import { useTemplateRef } from 'vue'

const model = defineModel<boolean>()

interface SaveData {
	mainImage?: string
	otherImages?: string[]
}
const emit = defineEmits<{
	save: [value: SaveData]
}>()

const props = defineProps<{
	mainImageLabel?: string,
	otherImagesLabel?: string
}>()

const mainPond = useTemplateRef<FilePondType>('mainPond')
const otherPond = useTemplateRef<FilePondType>('otherPond')

const serverConfig = {
	process: apiRoot + 'filepond/process',
	revert: apiRoot + 'filepond/revert'
}

function showDialog(e: Event): void {
	e.preventDefault()
	model.value = true
}

const titleLabel = computed(() => props.otherImagesLabel
	? 'draganddrop.dialog.title.multiple'
	: 'draganddrop.dialog.title.single')

function cancel(): void {
	if (mainPond.value) {
		mainPond.value.removeFiles()
	}
	if (otherPond.value) {
		otherPond.value.removeFiles()
	}
	model.value = false
}

function getServerIds(filepond: FilePondType | null): string[] | undefined {
	if (!filepond) {
		return undefined
	}
	const objects = filepond.getFiles()
	if (!objects?.length) {
		return undefined
	}
	return objects.map(e => e.serverId)
}

function save(): void {
	const mainIds = getServerIds(mainPond.value)
	let mainId
	if (mainIds) {
		mainId = mainIds[0]
	}
	const otherIds = getServerIds(otherPond.value)
	const saveData: SaveData = {
		mainImage: mainId,
		otherImages: otherIds
	}
	// To ease processing in callee, only send the save event if there is data to save
	if (mainId || otherIds) {
		console.log('Data to save', saveData)
		emit('save', saveData)
	} else {
		console.debug('No data to save')
	}

	// Clear the dialog
	cancel()
}

onMounted(() => {
	console.debug('Adding image drag-and-drop event handler')
	document.body.addEventListener('dragenter', showDialog)
})

onBeforeUnmount(() => {
	console.debug('Removing image drag-and-drop event handler')
	document.body.removeEventListener('dragenter', showDialog)
})
</script>

<style lang="scss">
.filepond--credits {
	// Hide the credits, we have a nice entry in the About page for that
	display: none;
}
</style>
