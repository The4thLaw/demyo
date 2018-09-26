(function($) {
	'use strict';
	
	var contextRoot = $('head').data('context-root');
	if (!contextRoot.match(/.*\/$/)) {
		contextRoot += '/';
	}
	
	
})(jQuery);

jQuery(function () {
	$('#qt-add-images-to-album').click(function () {
		var dialog = $('<dialog class="mdl-dialog"></dialog>');
		dialog.append('<h4 class="mdl-dialog__title">' + 'TODO: title' + '</h4>');
		dialog.append('<div class="mdl-dialog__content">' +
				'<div class="dem-form-value__field">' +
					'<label class="dem-form__label">' + 'TODO: cover' + '</label>' +
					'<div class="dem-form__value">TODO: filepond</div>' +
				'</div>' +
				'<div class="dem-form-value__field">' +
					'<label class="dem-form__label">' + 'TODO: other images' + '</label>' +
					'<div class="dem-form__value">TODO: filepond</div>' +
				'</div>'
			);
		dialog.append('<div class="mdl-dialog__actions">' +
				'<button type="button" class="mdl-button mdl-button--primary confirm">' + 'TODO: OK' + '</button>' +
				'<button type="button" class="mdl-button mdl-button--primary cancel">' + 'TODO: Cancel' + '</button>' +
				'</div>');
		
		// Register the events
		$('.confirm', dialog).click(function () {
			$('<form method="post" action="'+url+'" style="display:none;"></form>')
				.insertBefore('#page-content')
				.submit();
		})
		$('.cancel', dialog).click(function () {
			dialog.get(0).close();
		})
		
		// Append the dialog to the body
		$('body').append(dialog);
		
		// Register and show the dialog
		if (!dialog.showModal) {
			dialogPolyfill.registerDialog(dialog.get(0));
		}
		dialog.get(0).showModal();
		return false;
	});
});
