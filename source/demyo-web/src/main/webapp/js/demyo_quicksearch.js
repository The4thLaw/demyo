(function($) {
	'use strict';
	
	$.fn.extend({
		demyo_quickSearch: function(settings) {
			if (!this.length) {
				return this;
			}
			
			settings = $.extend({
				delay: 650,
				url: '/search/quick',
				contentToHide: '#page-content',
				resultLocation: '#quickSearch-results',
				fadeSpeed: 'fast',
				reportProgressOn: '.mdl-layout__content'
			}, settings);

			if (settings.contentToHide == '') {
				settings.contentToHide = '#no-such-element';
			}
			
			function loading() {
				$(settings.reportProgressOn)
					.prepend('<div id="quickSearch-progress-indicator" style="width: 100%; position: absolute;" class="mdl-progress mdl-js-progress mdl-progress__indeterminate"></div>');
				componentHandler.upgradeElement($('#quickSearch-progress-indicator').get(0));
			};
			
			function loaded() {
				$('#quickSearch-progress-indicator').remove();
			};
			
			function processResults(data) {
				$(settings.resultLocation).html(data);
				// Select the first result
				$($('a:first-child', $(settings.resultLocation)).get(0)).focus();
				// Done
				loaded();
			};
			
			function hideContent() {
				$(settings.contentToHide).fadeOut(settings.fadeSpeed);
				$(settings.resultLocation).fadeIn(settings.fadeSpeed);
				$('body').addClass('quickSearch-active');
			};
			
			function restoreContent() {
				$(settings.resultLocation).fadeOut(settings.fadeSpeed, function () {
					$(settings.resultLocation).html('');
					$(settings.contentToHide).fadeIn(settings.fadeSpeed);
					$('body').removeClass('quickSearch-active');
				});
			};
			
			function query(str) {
				str = str.trim();
				
				if (str == '') {
					restoreContent();
					return;
				}
				
				loading();
				hideContent();
				
				$.ajax({
					url: settings.url,
					data: { q: str },
					success: processResults
				});
			};
			
			// Init
			this.keyup(function () {
				var $input = $(this);
				
				if (this.timer) {
					clearTimeout(this.timer);
				}
				
				this.timer = setTimeout(function() {
						query($input.val());
					}, settings.delay)
			})
		}
	});
})(jQuery);

jQuery(function () {
	$('#quicksearch-menu-field, #quicksearch-header-field').demyo_quickSearch();
	$('#quicksearch-widget-field').demyo_quickSearch({
		contentToHide: '',
		resultLocation: '#widget-search-results',
		reportProgressOn: '#widget-search'
	});
});
