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
		$('.dem-colour-remover input').change(function (e) {
			var self = $(this);
			$('#' + self.data('for')).prop('disabled', self.is(':checked'));
			$('#' + self.data('for')).change(); // Trigger event
		});
		// Trigger at start to disable the input if needed
		$('.dem-colour-remover input').change();
	};
	
	/**
	 * Binds all select elements to used Chosen.js
	 */
	demyo.bindSelectInputs = function () {
		var elementSet = $('select');
		elementSet.chosen({
			// TODO: find a way to set no_results_text
			allow_single_deselect: true,
			disable_search_threshold: 5,
			search_contains: true,
			display_selected_options: false,
			placeholder_text_single: ' ',
			placeholder_text_multiple: ' ',
			width: '100%'
		});
		// TODO: if chosen is disabled in this device, stop here
		elementSet.each(function () {
			// Add a special class to all labels, to style them
			$('label[for='+$(this).attr('id')+']').addClass('chosen-label')
		});
		var changeTrigger = function (evt, params) {
			var chosenCtn = $('~ .chosen-container', this);
			var isSingle = chosenCtn.is('.chosen-container-single');
			var singleIsDirty = $('.chosen-default', chosenCtn).length <= 0;
			var isMulti = chosenCtn.is('.chosen-container-multi');
			// Event is fired too soon by plugin: the last element is still there. We need a workaround
			var multiIsDirty;
			if (typeof(params) == 'undefined') {
				multiIsDirty = false;
			} else if (typeof(params.selected) != 'undefined') {
				// If we selected something, the element is always dirty
				multiIsDirty = true;
			} else if (typeof(params.deselected) != 'undefined') {
				// On deselection, the removed element remains until after the callback
				multiIsDirty = $('.search-choice', chosenCtn).length > 1;
			} else {
				// Initial page load
				multiIsDirty = $('.search-choice', chosenCtn).length > 0;
			}
			
			var mdlTextField = $($(this).parents('.mdl-textfield').get(0));
			if (isSingle && singleIsDirty || isMulti && multiIsDirty) {
				mdlTextField.addClass('is-dirty');
			} else {
				mdlTextField.removeClass('is-dirty');
			}
		};
		// Bind handler
		elementSet.change(changeTrigger);
		// Trigger the change event initially to mark fields as dirty at load time if needed
		elementSet.each(changeTrigger);
	};
	
	demyo.dependentSelect = function (mainSelector, dependentSelector, urlBuilder) {
		var main = $(mainSelector), sub = $(dependentSelector);
		
		var refresher = function () {
			var mainValue = main.val();
			if (mainValue == '') {
				sub.html('<option value=""></option>');
				sub.trigger('chosen:updated');
				sub.change();
			} else {
				jQuery.ajax({
					url: urlBuilder(mainValue),
					contentType: 'application/json',
					dataType: 'json',
					success: function (data, status, jqXHR) {
						console.log('Successfully got the list of dependent items (HTTP ' + jqXHR.status + ')')
						sub.html('<option value=""></option>');
						jQuery(data).each(function (index, value) {
							sub.append('<option value="' + value.id + '">' + value.identifyingName + '</option>');
						});
						// Trigger events
						sub.trigger('chosen:updated');
						sub.change();
					}
				});
			}
		};
		
		// Bind event and trigger it already
		main.change(refresher);
		refresher();
	};

})(jQuery);

jQuery(function () {
	demyo.bindValidationHandlers();
	demyo.bindColourInputs();
	demyo.bindSelectInputs();
});
