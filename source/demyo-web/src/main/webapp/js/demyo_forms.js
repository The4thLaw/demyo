/**
 * Common tools used in Demyo forms.
 */


(function($) {

	/*
	 * Clears validation errors on change.
	 * This is broken for TinyMCE but validation on such field is currently not performed in Demyo
	 */
	$(function() {
		$('form .value .error').change(function() {
			self = $(this);
			self.removeClass('error');
			$('.validation-error', self.parents('.value').get(0)).remove();
		});
	});
	
})(jQuery);