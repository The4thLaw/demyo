/**
 * Common tools used in Demyo forms.
 */

(function($) {
	'use strict';
	
	/**
	 * Clears validation errors on change.
	 * This is broken for TinyMCE but validation on such field is currently not performed in Demyo
	 */
	demyo.bindValidationHandlers = function () {
		$('form .dem-field-error input').change(function () {
			var self = $(this);
			var parent = $(self.parents('.dem-field-error').get(0));
			parent.removeClass('dem-field-error');
			$('.mdl-textfield__error', parent).remove();
		});
	};
	
	/**
	 * Clears the colour inputs on demand
	 */
	demyo.bindColourInputs = function () {
		$('.colour-remover input').change(function (e) {
			var self = $(this);
			$('#' + self.data('for')).prop('disabled', self.is(':checked'));
			$('#' + self.data('for')).change(); // Trigger event
		});
		// Trigger at start to disable the input if needed
		$('.colour-remover input').change();
	};
	
	/**
	 * Binds all select elements to used Chosen.js
	 */
	demyo.bindSelectInputs = function () {
		var elementSet = $('select');
		elementSet.chosen({
			// TODO: find a way to set no_results_text, placeholder_text_multiple, placeholder_text_single. Placeholder can be set through data-placeholder
			allow_single_deselect: true,
			disable_search_threshold: 5,
			search_contains: true,
			display_selected_options: false,
			width: '100%'
		});
		elementSet.each(function() {
			// Add a special class to all labels, to style them
			$('label[for='+$(this).attr('id')+']').addClass('chosen-label')
		});
	};

})(jQuery);

jQuery(function () {
	demyo.bindValidationHandlers();
	demyo.bindColourInputs();
	demyo.bindSelectInputs();
});
