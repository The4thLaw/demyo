(function($) {
$(function () {
	'use strict';
	
	var contextRoot = $('head').data('context-root');
	if (!contextRoot.match(/.*\/$/)) {
		contextRoot += '/';
	}
	
	FilePond.registerPlugin(
	    FilePondPluginFileValidateType
	);
	FilePond.setOptions({
		server: {
			process: contextRoot + 'api/filepond/process',
			revert: contextRoot + 'api/filepond/revert'
		},
		acceptedFileTypes: ['image/*'],
		// Labels
		fileValidateTypeLabelExpectedTypes: demyo.l10n['draganddrop.filepond.fileValidateTypeLabelExpectedTypes'],
		labelButtonAbortItemLoad: demyo.l10n['draganddrop.filepond.labelButtonAbortItemLoad'],
		labelButtonAbortItemProcessing: demyo.l10n['draganddrop.filepond.labelButtonAbortItemProcessing'],
		labelButtonProcessItem: demyo.l10n['draganddrop.filepond.labelButtonProcessItem'],
		labelButtonRemoveItem: demyo.l10n['draganddrop.filepond.labelButtonRemoveItem'],
		labelButtonRetryItemLoad: demyo.l10n['draganddrop.filepond.labelButtonRetryItemLoad'],
		labelButtonRetryItemProcessing: demyo.l10n['draganddrop.filepond.labelButtonRetryItemProcessing'],
		labelButtonUndoItemProcessing: demyo.l10n['draganddrop.filepond.labelButtonUndoItemProcessing'],
		labelFileLoadError: demyo.l10n['draganddrop.filepond.labelFileLoadError'],
		labelFileLoading: demyo.l10n['draganddrop.filepond.labelFileLoading'],
		labelFileProcessing: demyo.l10n['draganddrop.filepond.labelFileProcessing'],
		labelFileProcessingAborted: demyo.l10n['draganddrop.filepond.labelFileProcessingAborted'],
		labelFileProcessingComplete: demyo.l10n['draganddrop.filepond.labelFileProcessingComplete'],
		labelFileProcessingError: demyo.l10n['draganddrop.filepond.labelFileProcessingError'],
		labelFileSizeNotAvailable: demyo.l10n['draganddrop.filepond.labelFileSizeNotAvailable'],
		labelFileTypeNotAllowed: demyo.l10n['draganddrop.filepond.labelFileTypeNotAllowed'],
		labelFileWaitingForSize: demyo.l10n['draganddrop.filepond.labelFileWaitingForSize'],
		labelIdle: demyo.l10n['draganddrop.filepond.labelIdle'],
		labelTapToCancel: demyo.l10n['draganddrop.filepond.labelTapToCancel'],
		labelTapToRetry: demyo.l10n['draganddrop.filepond.labelTapToRetry'],
		labelTapToUndo: demyo.l10n['draganddrop.filepond.labelTapToUndo']
	});
	// TODO: when cancelling, revert all files

	// TODO: add handler on the body element (if one of the quicktasks exists)
	
	// TODO: this should be extracted to a method:
	// handleDndUpload(selector, formAction, withMainImage, withOtherImages, labels)
	$('#qt-add-images-to-album').click(function () {
		var dialog = $('#filepond-dialog');
		if (dialog.length) {
			console.log('The DnD dialog already exists, not doing anything');
			return;
		}
		
		var albumId = $('#album_id').val();
		dialog = $('<dialog id="filepond-dialog" class="mdl-dialog"></dialog>');
		var form = $('<form method="post" action="' + contextRoot + 'albums/' + albumId + '/filepond"></form>');
		form.append('<h4 class="mdl-dialog__title">' + demyo.l10n['draganddrop.dialog.title'] + '</h4>');
		form.append('<div class="mdl-dialog__content">' +
				'<div class="dem-form-value__field">' +
					'<label class="dem-form__label">' + 'TODO: cover' + '</label>' +
					'<div class="dem-form__value"><input type="file" name="filePondMainImage" class="filepond"></div>' +
				'</div>' +
				'<div class="dem-form-value__field">' +
					'<label class="dem-form__label">' + 'TODO: other images' + '</label>' +
					'<div class="dem-form__value"><input type="file" name="filePondOtherImage" class="filepond" multiple></div>' +
				'</div>'
			);
		dialog.append(form);
		dialog.append('<div class="mdl-dialog__actions">' +
				'<button type="button" class="mdl-button mdl-button--primary cancel">' + demyo.l10n['draganddrop.button.cancel'] + '</button>' +
				'<button type="button" class="mdl-button mdl-button--accent mdl-button--raised confirm">' + demyo.l10n['draganddrop.button.confirm'] + '</button>' +
				'</div>');
		
		// Register FilePond
		var ponds = FilePond.parse(dialog.get(0));

		// Register the events
		$('.confirm', dialog).click(function () {
			$('form', dialog).submit();
			dialog.get(0).close();
		});
		$('.cancel', dialog).click(function () {
			dialog.get(0).close();
			ponds.forEach(function (item) {
				// TODO: this doesn't seem to revert the files
				// Maybe this is related to https://github.com/pqina/filepond/issues/47 ?
				item.removeFiles();
			});
			dialog.remove();
		});
		
		// Append the dialog to the body
		$('body').append(dialog);
		
		// Register and show the dialog
		if (!dialog.get(0).showModal) {
			dialogPolyfill.registerDialog(dialog.get(0));
		}
		dialog.get(0).showModal();
		return false;
	});
});
})(jQuery);