/**
 * Common tools used in Demyo forms.
 */


(function($) {

	/**
	 * Clears validation errors on change.
	 * This is broken for TinyMCE but validation on such field is currently not performed in Demyo
	 */
	$(function() {
		$('form .value .error').change(function () {
			var self = $(this);
			self.removeClass('error');
			$('.validation-error', self.parents('.value').get(0)).remove();
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