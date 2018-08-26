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
					demyo.showToast(demyo.l10n['readers.confirm.favourite.add']);
				} else {
					$('#qt-unfave-album').hide();
					$('#qt-unfave-series').hide();
					$('#qt-fave-album').show();
					$('#qt-fave-series').show();
					demyo.showToast(demyo.l10n['readers.confirm.favourite.remove']);
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
	
	demyo.readers.addOrRemoveReadingList = function() {
		var $self = $(this),
			type,
			isAdd;
		
		if (this.id.match(/-add-/)) {
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
					$('#qt-add-readingList').hide();
					$('#fab-remove-readingList').show();
					demyo.showToast(demyo.l10n['readers.confirm.readingList.add']);
				} else {
					$('#fab-remove-readingList').hide();
					$('#qt-add-readingList').show();
					demyo.showToast(demyo.l10n['readers.confirm.readingList.remove']);
				}
				$self.removeClass('dem-animate-progress');
			},
			error: function() {
				console.log('Failed to add or remove album to/from reading list');
				console.log(arguments);
				$self.removeClass('animate-progress');
			} 
		});
		return false;
	};
	
	demyo.readers.bindReadingListToggles = function () {
		var isReadingList = $('#is_readingList').val() == 'true';
		if (isReadingList) {
			$('#qt-add-readingList').hide();
			$('#fab-remove-readingList').show();
		} else {
			$('#qt-add-readingList').show();
			$('#fab-remove-readingList').hide();
		}
		
		$('#qt-add-readingList, #fab-remove-readingList').click(demyo.readers.addOrRemoveReadingList);
	};
	
})(jQuery);

jQuery(function () {
	demyo.readers.bindFavouriteToggles();
	demyo.readers.bindReadingListToggles();
});

