(function($) {
	'use strict';
	
	demyo.readers = {};
	
	demyo.readers.addOrRemoveFavourite = function() {
		var $self = $(this),
			type,
			isAdd;
		
		if (this.id.match(/-fave-/)) {
			type = 'POST';
			isAdd = true;
		} else {
			type = 'DELETE';
			isAdd = false;
		}
		var url = $self.attr('href');
		$self.addClass('dem-animate-progress');
		
		$.ajax({
			type: type,
			url: url,
			contentType: 'application/json',
			success: function() {
				if (isAdd) {
					$('#qt-fave-album').hide();
					$('#qt-fave-series').hide();
					$('#qt-unfave-album').show();
					$('#qt-unfave-series').show();
				} else {
					$('#qt-unfave-album').hide();
					$('#qt-unfave-series').hide();
					$('#qt-fave-album').show();
					$('#qt-fave-series').show();
				}
				$self.removeClass('dem-animate-progress');
			},
			error: function() {
				console.log('Failed to add or remove favourite');
				console.log(arguments);
				$self.removeClass('animate-progress');
			} 
		});
		return false;
	};
	
	demyo.readers.bindFavouriteToggles = function () {
		var isFavourite = $('#is_favourite').val() == 'true';
		if (isFavourite) {
			$('#qt-fave-album').hide();
			$('#qt-fave-series').hide();
		} else {
			$('#qt-unfave-album').hide();
			$('#qt-unfave-series').hide();
		}
		
		$('#qt-fave-album, #qt-fave-series, #qt-unfave-album, #qt-unfave-series')
			.click(demyo.readers.addOrRemoveFavourite);
	};
	
})(jQuery);

jQuery(function () {
	demyo.readers.bindFavouriteToggles();
});

