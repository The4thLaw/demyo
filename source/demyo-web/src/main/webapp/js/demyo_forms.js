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
		
		// Check if chosen is enabled
		if ($('.chosen-container').length <= 0) {
			// If not, mark the select elements as missing it, dirty, and add also a specific class to the label
			elementSet.addClass('chosen-not-available');
			elementSet.parents('.mdl-textfield').addClass('is-dirty')
			$('~ label', elementSet).addClass('chosen-not-available');

			// We also need to take care about a nasty iOS issue, where the first element is always selected but not marked as such
			if (/iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream) {
				elementSet.filter('[multiple]').prepend('<optgroup disabled hidden></optgroup>');
			}

			return;
		}
		
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
		var initialSubValue = sub.val();
		
		var refresher = function () {
			var mainValue = main.val();
			if (mainValue == '') {
				mainValue = null;
			}
			
			var builtUrl = urlBuilder(mainValue);
			
			if (builtUrl == null) {
				console.log('No dependent entries for main value "' + mainValue + '";')
				sub.html('<option value=""></option>');
				sub.trigger('chosen:updated');
				sub.change();
			}
			
			jQuery.ajax({
				url: urlBuilder(mainValue),
				contentType: 'application/json',
				dataType: 'json',
				success: function (data, status, jqXHR) {
					console.log('Successfully got the list of dependent items (HTTP ' + jqXHR.status + ')')
					sub.html('<option value=""></option>');
					jQuery(data).each(function (index, value) {
						sub.append('<option value="' + value.id + '" '
								+ ((value.id == initialSubValue)?'selected="selected"':'')
								+ '>' + value.identifyingName + '</option>');
					});
					// Trigger events
					sub.trigger('chosen:updated');
					sub.change();
				}
			});
		};
		
		
		// Bind event and trigger it already
		main.change(refresher);
		refresher();
	};
	
	demyo.bindRepeatableParts = function () {
		$('.dem-repeatable-list').each(function () {
			var part = this;
			
			// Load and remove template from DOM so that it is not submitted
			var template = $('.dem-repeatable-template', this);
			// Downgrade if needed, else future upgrades won't work
			componentHandler.downgradeElements(template.get(0));
			$('*', template).each(function () {
				componentHandler.downgradeElements(this);
			});
			var templateHtml = '<div class="dem-repeatable-item">' + template.html() + '</div>';
			template.remove();
			
			var removerCallback = function () {
				$($(this).parents('.dem-repeatable-item').get(0)).remove();
				demyo.renumberRepeatablePart(part);
				return false;
			};
			
			// Bind handler for removal of a specific part
			$('.dem-repeatable-remover').click(removerCallback);
			
			// Bind addition handler
			$('.dem-repeatable-adder').click(function () {
				$(this).before(templateHtml);
				var addedElement = $('.dem-repeatable-item', part).last();
				
				// Register component to MDL, and all its children too
				componentHandler.upgradeElement(addedElement.get(0));
				$('*', addedElement).each(function () {
					componentHandler.upgradeElement(this);	
				});
				
				// Register remover
				$('.dem-repeatable-remover', addedElement).click(removerCallback);
				
				demyo.renumberRepeatablePart(part);
				
				return false;	
			});
		});
	};
	
	demyo.renumberRepeatablePart = function (part) {
		var renumber = function (value, i) {
			return value.replace(/\[[0-9]*\]/, '[' + i + ']');
		};
		
		$('.dem-repeatable-item').each(function (itemNumber) {
			$('input', this).each(function () {
				var $this = $(this);
				$this.attr('id', renumber($this.attr('id'), itemNumber));
				$this.attr('name', renumber($this.attr('name'), itemNumber));
			});
			$('label', this).each(function () {
				var $this = $(this);
				$this.attr('for', renumber($this.attr('for'), itemNumber));
			});
		});
	};
	
	demyo.initTinyMCE = function () {
		var lang = $('body').data('tinymce-lang');
		
		console.log('Initialising TinyMCE in language ' + lang);
		
		// The following does not seem to be needed any more
		//$('textarea.richtext').css('opacity', 0);
		// TODO: set the content_security_policy ?
		tinymce.init({
			selector: 'textarea.richtext',
			plugins: 'autolink autoresize charmap code colorpicker link lists nonbreaking paste tabfocus table textcolor textpattern visualchars',
			language: lang,
			menu: {
				edit: {title: 'Edit', items: 'undo redo | cut copy paste pastetext | selectall'},
				insert: {title: 'Insert', items: 'link charmap nonbreaking'},
				format: {title: 'Format', items: 'bold italic underline strikethrough superscript subscript | formats | removeformat unlink'},
				table: {title: 'Table', items: 'inserttable tableprops deletetable | cell row column'},
				tools: {title: 'Tools', items: 'visualaid visualchars openlink | code'}
			},
			toolbar: 'bold italic underline forecolor backcolor | bullist numlist outdent indent blockquote | link',
			statusbar: false
		});
	};
	
	demyo.prepareTinyMCE = function () {
		if ($('textarea.richtext').length <= 0) {
			console.log('No richtext fields to upgrade');
			return;
		}
		// Material starts initialising its components on load. We need to be ready to catch the event before it starts
		// sending them, or trigger it if we're already too late (behaviour can depend on the version of jQuery)
		
		// Bind events
		console.log('Binding initialisation events for TinyMCE');
		document.addEventListener('mdl-componentupgraded', function(e) {
			//In case other element are upgraded before the layout  
			if (typeof e.target.MaterialLayout !== 'undefined') {
				demyo.initTinyMCE();
			}
		});
		
		if ($('.mdl-layout').is('.is-upgraded')) {
			// Fire it already, the upgrade has already happened
			demyo.initTinyMCE();
		}
	};
	
	$.fn.extend({
		disableIf: function(settings) {
			if (!this.length) {
				return this;
			}
			var self = this;
			
			var $elems = $(settings.selector);
			if ($elems.length > 1) {
				console.log('Cannot bind a disableIf to more than one source element');
				return this;
			}
			if (!$elems.length) {
				console.log('No source element to bind the disableIf to')
				return this;
			}
			
			var changeHandler = function () {
				var checked = $(this).is(':checked');
				self.prop('disabled', checked);
				if (checked) {
					self.parent().addClass('is-disabled');
				} else {
					self.parent().removeClass('is-disabled');
				}
			};

			$elems.change(changeHandler);
			
			// Trigger initially
			changeHandler.call($elems.get());
		}
	});

})(jQuery);

jQuery(function () {
	demyo.bindValidationHandlers();
	demyo.bindColourInputs();
	demyo.bindSelectInputs();
	demyo.bindRepeatableParts();
	demyo.prepareTinyMCE();
});

