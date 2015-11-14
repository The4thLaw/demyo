var demyo = {};

(function($) {
	'use strict';
	
	demyo.maxDependenciesPerCard = 4;

	demyo.registerMenu = function () {
		// Collapse on click
		$('#main-menu [data-dem-collapses]').click(function () {
			var collapser = $(this);
			var collapsed = $(collapser.data('dem-collapses'));
			collapsed.slideToggle('fast');
			collapser.toggleClass('dem-collapsed');
		});
		
		// Collapse some elements initially
		$('#main-menu [data-dem-collapses][data-dem-collapsed-initially]').each(function() {
			var collapser = $(this);
			var collapsed = $(collapser.data('dem-collapses'));
			collapsed.hide();
			collapser.addClass('dem-collapsed');
		});
	};
	
	demyo.registerCollapsibleCards = function () {
		$('.dem-model-card:not(.dem-upgraded)').each(function () {
			var card = $(this);
			card.addClass('dem-upgraded');
			
			var depList = $('.dem-model-card__dependencies', this);
			if ($('li', depList).length <= demyo.maxDependenciesPerCard) {
				return true;
			}
			var hiddenDeps = $('li:nth-child(n+' + demyo.maxDependenciesPerCard + ')', depList);
			hiddenDeps.hide();
			
			card.append('<div class="mdl-card__actions dem-dependencies-exander">'
					+ '<button class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"><i class="dico">ellipsis_h</i></button></div>');
			
			$('.dem-dependencies-exander > button', card).click(function () {
				$('.dem-dependencies-exander', card).remove();
				hiddenDeps.slideDown('fast');
			});
		});
	};
	
	demyo.registeredShortcuts = {};
	demyo.registerShortcuts = function () {
		$('[data-dem-shortcut]').each(function () {
			var trigger = $(this);
			demyo.registeredShortcuts['key_' + trigger.data('dem-shortcut')] = trigger;
		});
		$(document).on('keydown', function (evt) {
			if (evt.altKey || evt.ctrlKey || evt.metaKey || evt.shiftKey) {
				// always ignore combinations, we don't support them at the moment
				return true;
			}
			
			var target = $(evt.target);
			if (target.is('input,textarea,select,option')) {
				// Always ignore events in input fields
				return true;
			}
			
			var trigger = demyo.registeredShortcuts['key_' + evt.keyCode];
			if (typeof(trigger) === 'undefined') {
				return true;
			} else {
				if (trigger.is('a')) {
					var url = trigger.attr('href');
					window.location = url;
				} else {
					trigger.click();
				}
				return false;
			}
		});
	};
	
})(jQuery);

jQuery(function () {
	demyo.registerMenu();
	demyo.registerCollapsibleCards();
	demyo.registerShortcuts();
});