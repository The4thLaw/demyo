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

	var dndUploadHandler = function(formAction, withMainImage, withOtherImages, labels) {
		var dialog = $('#filepond-dialog');
		if (dialog.length) {
			console.log('The DnD dialog already exists, not doing anything');
			return;
		}
		
		dialog = $('<dialog id="filepond-dialog" class="mdl-dialog"></dialog>');
		var form = $('<form method="post" action="' + formAction + '"></form>');
		form.append('<h4 class="mdl-dialog__title">' + demyo.l10n['draganddrop.dialog.title'] + '</h4>');
		var content = $('<div class="mdl-dialog__content"></div>');
		if (withMainImage) {
			content.append('<div class="dem-form-value__field">' +
					'<label class="dem-form__label">' + labels.mainImageLabel + '</label>' +
					'<div class="dem-form__value"><input type="file" name="filePondMainImage" class="filepond"></div>' +
				'</div>'
			);
		}
		if (withOtherImages) {
			content.append('<div class="dem-form-value__field">' +
					'<label class="dem-form__label">' + labels.otherImagesLabel + '</label>' +
					'<div class="dem-form__value"><input type="file" name="filePondOtherImage" class="filepond" multiple></div>' +
				'</div>'
			);
		}
		form.append(content);
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
		});
		$('.cancel', dialog).click(function () {
			dialog.get(0).close();
		});
		
		// Append the dialog to the body
		$('body').append(dialog);
		
		// Register and show the dialog
		if (!dialog.get(0).showModal) {
			dialogPolyfill.registerDialog(dialog.get(0));
		}
		dialog.on('close', function() {
			// FilePond doesn't seem to expose an API to revert the files so we do it with a bit of a hack
			$('.filepond--action-revert-item-processing').click();
			dialog.remove();
		});
		dialog.get(0).showModal();
		return false;
	};

	// Add a marker class to all DnD handlers for convenience in the next steps
	$('#qt-add-images-to-album, #qt-add-images-to-derivative').addClass('dnd-handler');

	// If there is a handler, activate it when something is dragged over the page body
	if ($('.dnd-handler').length > 0) {
		$('body')[0].addEventListener("dragenter", function(e) {
			e.preventDefault();
			if ($('#filepond-dialog').length <= 0) {
				$('.dnd-handler').click();
			}
			// Else do nothing: the dialog is already displayed
		});
	}
	
	// Specific handler for Albums
	$('#qt-add-images-to-album').click(function() {
		var albumId = $('#album_id').val();
		return dndUploadHandler(
			contextRoot + 'albums/' + albumId + '/filepond',
			true,
			true,
			{
				mainImageLabel: demyo.l10n['field.Album.cover'],
				otherImagesLabel: demyo.l10n['field.Album.images']
			}
		);
	});
	
	// Specific handler for Derivatives
	$('#qt-add-images-to-derivative').click(function() {
		var derivativeId = $('#derivative_id').val();
		return dndUploadHandler(
			contextRoot + 'derivatives/' + derivativeId + '/filepond',
			false,
			true,
			{
				otherImagesLabel: demyo.l10n['field.Derivative.images']
			}
		);
	});
});
})(jQuery);