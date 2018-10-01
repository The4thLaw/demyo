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
		acceptedFileTypes: ['image/*']
	});
	// TODO: set labels: https://pqina.nl/filepond/docs/patterns/api/filepond-instance/ + https://pqina.nl/filepond/docs/patterns/plugins/file-validate-type/#properties
	
	$('#qt-add-images-to-album').click(function () {		
		var dialog = $('<dialog id="filepond-dialog" class="mdl-dialog"><form method="post" action="' +
				window.location.href + '/filepond"></form></dialog>');
		dialog.append('<h4 class="mdl-dialog__title">' + 'TODO: title' + '</h4>');
		dialog.append('<div class="mdl-dialog__content">' +
				'<div class="dem-form-value__field">' +
					'<label class="dem-form__label">' + 'TODO: cover' + '</label>' +
					'<div class="dem-form__value"><input type="file" name="filePondMainImage" class="filepond"></div>' +
				'</div>' +
				'<div class="dem-form-value__field">' +
					'<label class="dem-form__label">' + 'TODO: other images' + '</label>' +
					'<div class="dem-form__value"><input type="file" name="filePondOtherImage" class="filepond" multiple></div>' +
				'</div>'
			);
		dialog.append('<div class="mdl-dialog__actions">' +
				'<button type="button" class="mdl-button mdl-button--primary confirm">' + 'TODO: OK' + '</button>' +
				'<button type="button" class="mdl-button mdl-button--primary cancel">' + 'TODO: Cancel' + '</button>' +
				'</div>');
		
		// Register the events
		$('.confirm', dialog).click(function () {
			$('form', dialog).submit();
			dialog.get(0).close();
		});
		$('.cancel', dialog).click(function () {
			dialog.get(0).close();
		});
		
		// Append the dialog to the body
		$('body').append(dialog);
		
		// Register FilePond
		FilePond.parse(dialog.get(0));
		
		// Register and show the dialog
		if (!dialog.get(0).showModal) {
			dialogPolyfill.registerDialog(dialog.get(0));
		}
		dialog.get(0).showModal();
		return false;
	});
});
})(jQuery);