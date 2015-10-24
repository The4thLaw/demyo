/**
 * Common tools used in Demyo forms.
 */


(function($) {

	/**
	 * Clears validation errors on change.
	 * This is broken for TinyMCE but validation on such field is currently not performed in Demyo
	 */
	$(function() {
		$('form .dem-field-error input').change(function () {
			var self = $(this);
			var parent = $(self.parents('.dem-field-error').get(0));
			parent.removeClass('dem-field-error');
			$('.mdl-textfield__error', parent).remove();
		});
	});
	
	/**
	 * Clears the colour inputs on demand
	 */
	$(function() {
		$('.colour-remover input').change(function (e) {
			var self = $(this);
			$('#' + self.data('for')).prop('disabled', self.is(':checked'));
			$('#' + self.data('for')).change(); // Trigger event
		});
		// Trigger at start to disable the input if needed
		$('.colour-remover input').change();
	});
	
})(jQuery);