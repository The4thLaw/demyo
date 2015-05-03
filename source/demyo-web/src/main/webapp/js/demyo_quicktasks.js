/**
 * Utilities for quick tasks management.
 */

(function($) {
$(function() {
	
	$('#quick-tasks [data-qt-confirm]').each(function() {
		$this = $(this);
		url = $('a', this).attr('href');
		$this.click(function() {
			// TODO: use jQuery UI for this
			if (confirm($this.data('qt-confirm'))) {
				$('<form method="post" action="'+url+'" style="display:none;"></form>')
					.insertBefore('#quick-tasks')
					.submit();
				return false;
			} else {
				return false;
			}
		})
	});
	
})
})(jQuery);