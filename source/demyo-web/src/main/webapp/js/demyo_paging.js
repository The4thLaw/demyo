/**
 * Tools for pagination.
 */

(function($) {
	
	// Links to previous or next
	function jumpToPage(evt) {
		// Check if a colorbox is currently active. If so, Ignore the goto
		var box = jQuery('div#colorbox');
		if (box.size() > 0 && box.css('display') != 'none')
			return;
		
		var url = null;
		if (evt.data.keys === 'left') {
			url = $('#page-link-prev').attr('href');
		} else if (evt.data.keys === 'right') {
			url = $('#page-link-next').attr('href');
		}
		if (url != null && typeof(url) !== 'undefined') {
			window.location = url;			
		}
		
	}
	$(document).bind('keydown', 'left', jumpToPage);
	$(document).bind('keydown', 'right', jumpToPage);

})(jQuery);