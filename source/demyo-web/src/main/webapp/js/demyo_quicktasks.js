/**
 * Utilities for quick tasks management.
 */

(function($) {
$(function() {
	
	$('a[data-qt-confirm]').each(function() {
		var $this = $(this);
		var url = $this.attr('href');
		$this.click(function(e) {
			e.preventDefault();
			
			// Create MDL-style HTML5 dialog
			var dialog = $('<dialog class="mdl-dialog"></dialog>');
			dialog.append('<h4 class="mdl-dialog__title">' + demyo.l10n['quickTasks.confirm.title'] + '</h4>');
			dialog.append('<div class="mdl-dialog__content"><p>' + $this.data('qt-confirm') + '</p></div>');
			dialog.append('<div class="mdl-dialog__actions">' +
					'<button type="button" class="mdl-button mdl-button--accent mdl-button--raised confirm">' +
					demyo.l10n['quickTasks.confirm.ok.label'] + '</button>' +
					'<button type="button" class="mdl-button mdl-button--primary cancel">' +
					demyo.l10n['quickTasks.confirm.cancel.label'] + '</button>' +
					'</div>');
			
			// Register the events
			$('.confirm', dialog).click(function () {
				$('<form method="post" action="'+url+'" style="display:none;"></form>')
					.insertBefore('#page-content')
					.submit();
			});
			$('.cancel', dialog).click(function () {
				dialog.get(0).close();
			});
			
			// Append the dialog to the body
			$('body').append(dialog);
			
			// Register and show the dialog
			if (!dialog.showModal) {
				dialogPolyfill.registerDialog(dialog.get(0));
			}
			dialog.on('close', function() {
				dialog.remove();
			});
			dialog.get(0).showModal();
			
			return false;
		});
	});
	
});
})(jQuery);