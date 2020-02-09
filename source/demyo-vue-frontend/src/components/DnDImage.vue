<template>
	<v-dialog v-model="dialog" max-width="800px">
		<v-card>
			<v-card-title>
				{{ $t('draganddrop.dialog.title') }}
			</v-card-title>
			<v-card-text>
				<div v-if="mainImageLabel">
					<label class="fieldLabel">
						{{ $t(mainImageLabel) }}
					</label>
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
					<label class="fieldLabel">
						{{ $t(otherImagesLabel) }}
					</label>
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

				<v-btn color="accent" @click="save">
					{{ $t('draganddrop.button.confirm') }}
				</v-btn>

				<v-btn color="primary" text @click="cancel">
					{{ $t('draganddrop.button.cancel') }}
				</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>

<script>
import FilePond from '@/helpers/filepond'
import { apiRoot } from '@/myenv'

export default {
	name: 'DnDImage',

	components: {
		FilePond
	},

	props: {
		mainImageLabel: {
			type: String,
			default: null
		},
		otherImagesLabel: {
			type: String,
			default: null
		}
	},

	data() {
		return {
			dialog: true,
			serverConfig: {
				process: apiRoot + 'filepond/process',
				revert: apiRoot + 'filepond/revert'
			}
		}
	},

	created() {
		console.debug('Adding image drag-and-drop event handler')
		document.body.addEventListener('dragenter', this.showDialog)
	},

	beforeDestroy() {
		console.debug('Removing image drag-and-drop event handler')
		document.body.removeEventListener('dragenter', this.showDialog)
	},

	methods: {
		showDialog(e) {
			e.preventDefault()
			this.dialog = true
		},

		cancel() {
			if (this.$refs.mainPond) {
				this.$refs.mainPond.removeFiles()
			}
			if (this.$refs.otherPond) {
				this.$refs.otherPond.removeFiles()
			}
			this.dialog = false
		},

		save() {
			let mainId = this.getServerIds(this.$refs.mainPond)
			if (mainId) {
				mainId = mainId[0]
			}
			let otherIds = this.getServerIds(this.$refs.otherPond)
			let saveData = {
				mainImage: mainId,
				otherImages: otherIds
			}
			// To ease processing in callee, only send the save event if there is data to save
			if (mainId || otherIds) {
				console.log('Data to save', saveData)
				this.$emit('save', saveData)
			} else {
				console.debug('No data to save')
			}

			this.cancel()
		},

		getServerIds(filepond) {
			if (!filepond) {
				return null
			}
			let objects = filepond.getFiles()
			if (!objects || !objects.length) {
				return null
			}
			let ids = objects.map(e => e.serverId)
			return ids
		}
	}
}
</script>
